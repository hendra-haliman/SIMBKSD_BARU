package id.go.jakarta.bksd.dokumen.service;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaPredicate;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.DokumenKerjaSamaLuarNegeri;
import id.go.jakarta.bksd.dokumen.domain.DokumenKerjaSamaLuarNegeriOld;
import id.go.jakarta.bksd.dokumen.domain.Page;
import id.go.jakarta.bksd.dokumen.domain.datatables.PagingRequest;
import id.go.jakarta.bksd.dokumen.domain.dto.DokumenKerjaSamaLuarNegeriDTO;
import id.go.jakarta.bksd.dokumen.domain.dto.DokumenKerjaSamaLuarNegeriOldDTO;
import id.go.jakarta.bksd.dokumen.repository.DokumenKerjaSamaLuarNegeriOldRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class DokumenKerjaSamaLuarNegeriOldService {
    private DokumenKerjaSamaLuarNegeriOldRepository repository;
    private PemakaiService pemakaiService;

    @PersistenceContext
    private EntityManager entityManager;

    public DokumenKerjaSamaLuarNegeriOldService(
        DokumenKerjaSamaLuarNegeriOldRepository repository,
        PemakaiService pemakaiService) {
        this.repository = repository;
        this.pemakaiService = pemakaiService;
    }

    public DokumenKerjaSamaLuarNegeriOld save(DokumenKerjaSamaLuarNegeriOld doc) {
        return repository.save(doc);
    }

    public Page<DokumenKerjaSamaLuarNegeriOldDTO> getDokumen(PagingRequest pagingRequest) {
        int pageNo = pagingRequest.getStart() / pagingRequest.getLength();
        int pageSize = pagingRequest.getLength();

        return getDokumen2(pagingRequest, pageNo, pageSize);

    }

    public Page<DokumenKerjaSamaLuarNegeriOldDTO> getDokumen2(PagingRequest pagingRequest, int pageNo, int pageSize) {
        boolean canViewDocLink = pemakaiService.hasAuthority(PemakaiService.AUTH_CAN_VIEW_DOC_LINK);
        int filterTahun = pagingRequest.getFilterTahun();
        String searchTerm = "%" + pagingRequest.getSearch().getValue() + "%";

        HibernateCriteriaBuilder cb = entityManager.unwrap(Session.class).getCriteriaBuilder();

        JpaCriteriaQuery<DokumenKerjaSamaLuarNegeriOld> cq = cb.createQuery(DokumenKerjaSamaLuarNegeriOld.class);
        Root<DokumenKerjaSamaLuarNegeriOld> dokumenKerjaSamaLuarNegeriOld = cq.from(DokumenKerjaSamaLuarNegeriOld.class);

        Predicate searchObjekPredicate = cb.like(dokumenKerjaSamaLuarNegeriOld.get("objek"), searchTerm);
        Predicate searchMitraPredicate = cb.like(dokumenKerjaSamaLuarNegeriOld.get("mitra"), searchTerm);
        Predicate searchCakupanPredicate = cb.like(dokumenKerjaSamaLuarNegeriOld.get("cakupan"), searchTerm);
        Predicate searchDasarPredicate = cb.like(dokumenKerjaSamaLuarNegeriOld.get("dasar"), searchTerm);
        Predicate searchSKPDredicate = cb.like(dokumenKerjaSamaLuarNegeriOld.get("skpdUnitTerkait"), searchTerm);

        Predicate searchPredicate = cb.or(searchObjekPredicate, searchMitraPredicate, searchCakupanPredicate, searchDasarPredicate, searchSKPDredicate);

        Predicate deletedPredicate = cb.equal(dokumenKerjaSamaLuarNegeriOld.get("deleted"), false);

        Predicate filterTahunPredicate = cb.equal(dokumenKerjaSamaLuarNegeriOld.get("tahun"), filterTahun);


        JpaPredicate jpaPredicate = cb.and(searchPredicate, deletedPredicate, filterTahunPredicate);
        
        cq.where(jpaPredicate);

        cq.orderBy(
                cb.desc(dokumenKerjaSamaLuarNegeriOld.get("tahun")));
       
        cq.select(dokumenKerjaSamaLuarNegeriOld);
        
        List<DokumenKerjaSamaLuarNegeriOld> dokumenList = entityManager.createQuery(cq)
            .setFirstResult(pageNo * pageSize)
            .setMaxResults(pageSize)
            .getResultList();

        TypedQuery<Long> countQuery = entityManager.createQuery(cq.createCountQuery());

        Long totalElements = countQuery.getSingleResult();

        Page<DokumenKerjaSamaLuarNegeriOldDTO> page = new Page<>(
                dokumenList.stream().map(dks -> convertToDto(dks, canViewDocLink)).collect(Collectors.toList()));
        page.setRecordsFiltered((int) totalElements.longValue());
        page.setRecordsTotal((int) totalElements.longValue());
        page.setDraw(pagingRequest.getDraw());

        return page;
    }

    private DokumenKerjaSamaLuarNegeriOldDTO convertToDto(DokumenKerjaSamaLuarNegeriOld dokumenKerjaSama,
            boolean canViewDocLink) {

        DokumenKerjaSamaLuarNegeriOldDTO dto = new DokumenKerjaSamaLuarNegeriOldDTO();

        dto.setId(dokumenKerjaSama.getId());
        dto.setTahun(dokumenKerjaSama.getTahun());
        dto.setObjek(dokumenKerjaSama.getObjek());
        dto.setMitra(dokumenKerjaSama.getMitra());
        dto.setCakupan(dokumenKerjaSama.getCakupan());
        dto.setDasar(dokumenKerjaSama.getDasar());
        dto.setSkpdUnitTerkait(dokumenKerjaSama.getSkpdUnitTerkait());
        dto.setWaktu(dokumenKerjaSama.getWaktu());
        dto.setPelaksanaan(dokumenKerjaSama.getPelaksanaan());
        dto.setKendala(dokumenKerjaSama.getKendala());
        dto.setTindakLanjut(dokumenKerjaSama.getTindakLanjut());
        dto.setDeleted(dokumenKerjaSama.isDeleted());

        return dto;
    }

    public Optional<DokumenKerjaSamaLuarNegeriOld> findById(Long id) {
        return repository.findById(id);
    }

    public ByteArrayInputStream exportExcel(PagingRequest pagingRequest) {
        return null;
    }
}
