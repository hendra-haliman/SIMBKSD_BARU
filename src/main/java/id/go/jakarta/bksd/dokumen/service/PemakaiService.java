package id.go.jakarta.bksd.dokumen.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import id.go.jakarta.bksd.dokumen.domain.Page;
import id.go.jakarta.bksd.dokumen.domain.Pemakai;
import id.go.jakarta.bksd.dokumen.domain.PemakaiComparators;
import id.go.jakarta.bksd.dokumen.domain.Privilege;
import id.go.jakarta.bksd.dokumen.domain.Role;
import id.go.jakarta.bksd.dokumen.domain.datatables.Column;
import id.go.jakarta.bksd.dokumen.domain.datatables.Order;
import id.go.jakarta.bksd.dokumen.domain.datatables.PagingRequest;
import id.go.jakarta.bksd.dokumen.domain.dto.PemakaiDTO;
import id.go.jakarta.bksd.dokumen.repository.PemakaiRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PemakaiService<T> implements UserDetailsService {

    public static String AUTH_CAN_VIEW_DOC_LINK = "CAN_VIEW_DOC_LINK";

    @Autowired
    private HttpServletRequest request;

    private PemakaiRepository pemakaiRepository;

    private SessionFactory sessionFactory;

    private ModelMapper modelMapper;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private Random random = new Random();

    private static final Comparator<Pemakai> EMPTY_COMPARATOR = (e1, e2) -> 0;

    public PemakaiService(PemakaiRepository pemakaiRepository, SessionFactory sessionFactory, ModelMapper mapper) {
        this.pemakaiRepository = pemakaiRepository;
        this.sessionFactory = sessionFactory;
        this.modelMapper = mapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Pemakai pemakai = pemakaiRepository.findByUserName(username);
        if (pemakai == null) {
            throw new UsernameNotFoundException("Invalid email atau password");
        }

        return new User(pemakai.getEmail(),
                pemakai.getPassword(),
                pemakai.isEnabled(),
                true,
                true,
                true,
                getAuthorities(pemakai.getRoles()));

    }

    public boolean hasAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) {
        return getGrantedAuthorities(getPrivileges(roles));
    }

    private List<String> getPrivileges(Collection<Role> roles) {

        List<String> privileges = new ArrayList<>();
        List<Privilege> collection = new ArrayList<>();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        for (Role role : roles) {
            privileges.add(role.getName());
            collection.addAll(role.getPrivileges());
        }
        for (Privilege item : collection) {
            privileges.add(item.getName());
        }
        session.getTransaction().commit();
        session.close();
        return privileges;
    }

    public Pemakai findById(Long id) {
        return pemakaiRepository.findById(id).get();
    }

    public List<Pemakai> findAll() {
        return pemakaiRepository.findAll();
    }

    public Pemakai save(Pemakai pemakai) {
        return pemakaiRepository.save(pemakai);
    }

    public Pemakai findByEmail(String email) {
        return pemakaiRepository.findByUserName(email);
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

    public Pemakai findByUserName(String userName) {
        Pemakai pemakai = pemakaiRepository.findByUserName(userName);
        return pemakai;
    }

    public Page<PemakaiDTO> getPemakai(PagingRequest pagingRequest) {
        log.info("Before findsemua");
        int pageNo = pagingRequest.getStart() / pagingRequest.getLength();
        int pageSize = pagingRequest.getLength();
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        org.springframework.data.domain.Page<Pemakai> pemakaiPage = pemakaiRepository.findByKeyword(pageable,
                pagingRequest.getSearch().getValue());
        log.info("TOTAL records [{}]", pemakaiPage.getTotalElements());

        log.info("After findSemua");

        Page<PemakaiDTO> page = new Page<>(
                pemakaiPage.getContent().stream().map(this::convertToDto).collect(Collectors.toList()));
        page.setRecordsFiltered((int) pemakaiPage.getTotalElements());
        page.setRecordsTotal((int) pemakaiPage.getTotalElements());
        page.setDraw(pagingRequest.getDraw());

        return page;

    }

    private PemakaiDTO convertToDto(Pemakai pemakai) {
        PemakaiDTO pemakaiDTO = modelMapper.map(pemakai, PemakaiDTO.class);
        pemakaiDTO.setPrivileges(pemakai.getRoles().stream()
                .flatMap(role -> role.getPrivileges().stream())
                .map(Privilege::getName)
                .distinct()
                .collect(Collectors.toList()));
        return pemakaiDTO;
    }

    private Page<Pemakai> getPage(List<Pemakai> pemakaiList, PagingRequest pagingRequest) {

        List<Pemakai> filtered = pemakaiList.stream()
                // .sorted(sortPemakai(pagingRequest))
                .filter(filterPemakai(pagingRequest))
                .skip(pagingRequest.getStart())
                .limit(pagingRequest.getLength())
                .collect(Collectors.toList());

        long count = pemakaiList.stream()
                .filter(filterPemakai(pagingRequest))
                .count();

        Page<Pemakai> page = new Page<>(filtered);
        page.setRecordsFiltered((int) count);
        page.setRecordsTotal((int) count);
        page.setDraw(pagingRequest.getDraw());

        return page;
    }

    private Comparator<Pemakai> sortPemakai(PagingRequest pagingRequest) {
        if (pagingRequest.getOrder() == null) {
            return EMPTY_COMPARATOR;
        }

        try {
            Order order = pagingRequest.getOrder()
                    .get(0);

            int columnIndex = order.getColumn();
            Column column = pagingRequest.getColumns()
                    .get(columnIndex);

            Comparator<Pemakai> comparator = PemakaiComparators.getComparator(column.getData(), order.getDir());
            if (comparator == null) {
                return EMPTY_COMPARATOR;
            }

            return comparator;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return EMPTY_COMPARATOR;
    }

    @SuppressWarnings("deprecation")
    private Predicate<Pemakai> filterPemakai(PagingRequest pagingRequest) {
        if (pagingRequest.getSearch() == null || StringUtils.isEmpty(pagingRequest.getSearch()
                .getValue())) {
            return pemakai -> true;
        }

        String value = pagingRequest.getSearch()
                .getValue();

        return pemakai -> pemakai.getJabatan().toLowerCase().contains(value)
                || pemakai.getFullName().toLowerCase().contains(value)
                || pemakai.getEmail().toLowerCase().contains(value);
    }

    private String generateRandomHash(String input) {
        String hash = "";
        try {
            int seed = 1234592342; // The seed value

            Random random = new Random();
            // random.setSeed(seed);

            int randomNumber = random.nextInt();
            int randomNumber2 = random.nextInt();

            input = input + String.valueOf(randomNumber) + String.valueOf(randomNumber2);

            // Create an instance of the MessageDigest with the desired algorithm (e.g.,
            // "SHA-256")
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Convert the input string to bytes
            byte[] inputBytes = input.getBytes();

            // Update the digest with the input bytes
            digest.update(inputBytes);

            // Generate the hash as a byte array
            byte[] hashBytes = digest.digest();

            // Convert the hash bytes to a hexadecimal string
            StringBuilder hashBuilder = new StringBuilder();
            for (byte b : hashBytes) {
                hashBuilder.append(String.format("%02x", b));
            }
            hash = hashBuilder.toString();

            // Print the hash
            // System.out.println("Hash: " + hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }

    public String getFullUrl() {
        String scheme = request.getScheme(); // https
        String serverName = request.getServerName(); // localhost
        int serverPort = request.getServerPort(); // 8443
        String contextPath = request.getContextPath(); // /dokumen

        // Construct the full URL
        return scheme + "://" + serverName + ":" + serverPort + contextPath;
    }

    public String generateResetPasswordLink(Pemakai pemakai) {
        // String baseURL = "https://localhost:8443/dokumen/resetPassword2?v=";
        String baseURL = getFullUrl() + "/resetPassword2?v=";
        String randomString = generateRandomHash(pemakai.getEmail());

        pemakai.setResetStr(randomString);
        save(pemakai);

        return baseURL + randomString;
    }

    public Pemakai findByResetStr(String resetStr) {
        return pemakaiRepository.findByResetStr(resetStr);
    }

    /**
     * Get current authenticated username.
     * 
     * @return
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getName(); // Get the username of the authenticated user
    }

    private String generateRandomPassword() {
        // Generate a random alphanumeric password of length 10
        return random.ints(48, 122)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(10)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public Pemakai assignRandomPassword(Pemakai pemakai) {
        pemakai.setPassword(encryptPassword(generateRandomPassword()));
        return pemakai;
    }
}
