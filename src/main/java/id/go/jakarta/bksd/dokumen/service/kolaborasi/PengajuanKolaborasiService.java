package id.go.jakarta.bksd.dokumen.service.kolaborasi;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaPredicate;
import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.Page;
import id.go.jakarta.bksd.dokumen.domain.datatables.PagingRequest;
import id.go.jakarta.bksd.dokumen.domain.dto.kolaborasi.PengajuanKolaborasiDTO;
import id.go.jakarta.bksd.dokumen.domain.kolaborasi.PengajuanKolaborasi;
import id.go.jakarta.bksd.dokumen.repository.kolaborasi.PengajuanKolaborasiRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class PengajuanKolaborasiService {

    private PengajuanKolaborasiRepository repository;

    @PersistenceContext
    private EntityManager entityManager;


    public PengajuanKolaborasiService(PengajuanKolaborasiRepository repository) {
        this.repository = repository;
    }

    public PengajuanKolaborasi save(PengajuanKolaborasi kolaborasi) {
        return repository.save(kolaborasi);
    }

    public Optional<PengajuanKolaborasi> findById(Long id) {
        return repository.findById(id);
    }

    public Page<PengajuanKolaborasiDTO> getPengajuan(PagingRequest pagingRequest) {
        String searchTerm = pagingRequest.getSearch().getValue();
        int pageNo = pagingRequest.getStart();
        int pageSize = pagingRequest.getLength();

        HibernateCriteriaBuilder cb = entityManager.unwrap(Session.class).getCriteriaBuilder();
        JpaCriteriaQuery<PengajuanKolaborasi> cq = cb.createQuery(PengajuanKolaborasi.class);
        Root<PengajuanKolaborasi> pengajuan = cq.from(PengajuanKolaborasi.class);

        Predicate searchCriteria = cb.like(pengajuan.get("deskripsi_kolaborasi"), searchTerm);

        JpaPredicate predicate = cb.and(searchCriteria);

        cq.where(predicate);
        cq.select(pengajuan);

        List<PengajuanKolaborasi> pengajuanList = entityManager.createQuery(cq)
                                                    .setFirstResult(pageNo * pageSize)
                                                    .setMaxResults(pageSize)
                                                    .getResultList();
        TypedQuery<Long> countQuery = entityManager.createQuery(cq.createCountQuery());

        Long totalElements = countQuery.getSingleResult();

        Page<PengajuanKolaborasiDTO> page = new Page<>(
            pengajuanList.stream().map(p -> convertToDto(p)).collect(Collectors.toList()));
        page.setRecordsFiltered((int) totalElements.longValue());
        page.setRecordsTotal((int) totalElements.longValue());
        page.setDraw(pagingRequest.getDraw());

        return page;
    }

    private PengajuanKolaborasiDTO convertToDto(PengajuanKolaborasi pengajuan) {
        PengajuanKolaborasiDTO dto = new PengajuanKolaborasiDTO();
        dto.setId(pengajuan.getId());
        dto.setStatusPengajuan(pengajuan.getStatusPengajuan());
        dto.setPhotoKolaborasi(pengajuan.getPhotoKolaborasi());
        dto.setSubjekKerjaSama(pengajuan.getSubjekKerjaSama());
        dto.setJenisKolaborasi(pengajuan.getJenisKolaborasi());
        dto.setKolaborator(pengajuan.getKolaborator());
        dto.setProfilePIC(pengajuan.getProfilePIC());
        dto.setDeskripsi_kolaborasi(pengajuan.getDeskripsi_kolaborasi());
        dto.setSasaran(pengajuan.getSasaran());
        dto.setMitraKolaborator(pengajuan.getMitraKolaborator());
        dto.setEstimasiAnggaran(pengajuan.getEstimasiAnggaran());
        dto.setPeriodeKegiatan(pengajuan.getPeriodeKegiatan());
        dto.setDokumenPendukungKolaborasi(pengajuan.getDokumenPendukungKolaborasi());
        dto.setPernyataan(pengajuan.getPernyataan());

        return dto;
    }
}
