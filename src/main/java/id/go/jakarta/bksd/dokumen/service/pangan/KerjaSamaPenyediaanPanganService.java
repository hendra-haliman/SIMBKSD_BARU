package id.go.jakarta.bksd.dokumen.service.pangan;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaPredicate;
import org.springframework.stereotype.Service;

import id.go.jakarta.bksd.dokumen.domain.Page;
import id.go.jakarta.bksd.dokumen.domain.datatables.PagingRequest;
import id.go.jakarta.bksd.dokumen.domain.pangan.KerjaSamaPenyediaanPangan;
import id.go.jakarta.bksd.dokumen.domain.pangan.Pemda;
import id.go.jakarta.bksd.dokumen.repository.pangan.KerjaSamaPenyediaanPanganRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class KerjaSamaPenyediaanPanganService {
    private KerjaSamaPenyediaanPanganRepository repository;

    @PersistenceContext
    private EntityManager entityManager;


    public KerjaSamaPenyediaanPanganService(KerjaSamaPenyediaanPanganRepository repository) {
        this.repository = repository;
    }

    public KerjaSamaPenyediaanPangan save(KerjaSamaPenyediaanPangan kerjasama) {
        return repository.save(kerjasama);
    }

    public List<KerjaSamaPenyediaanPangan> findAll() {
        return repository.findAll();
    }

    public Page<KerjaSamaPenyediaanPangan> getKerjasama(PagingRequest pagingRequest) {
        int pageNo = pagingRequest.getStart() / pagingRequest.getLength();
        int pageSize = pagingRequest.getLength();

        return getKerjasama(pagingRequest, pageNo, pageSize);
    }

    public Page<KerjaSamaPenyediaanPangan> getKerjasama(PagingRequest pagingRequest, int pageNo, int pageSize) {
        int tahun = pagingRequest.getFilterTahun();
        String searchTerm =  "%" + pagingRequest.getSearch().getValue() + "%";

        HibernateCriteriaBuilder cb = entityManager.unwrap(Session.class).getCriteriaBuilder();
        JpaCriteriaQuery<KerjaSamaPenyediaanPangan> cq = cb.createQuery(KerjaSamaPenyediaanPangan.class);
        Root<KerjaSamaPenyediaanPangan> kerjasama = cq.from(KerjaSamaPenyediaanPangan.class);
        Join<KerjaSamaPenyediaanPangan, Pemda> pemdaJoin = kerjasama.join("pemda");
        
        Predicate tahunPredicate;
        if (tahun == 0) {
            tahunPredicate = cb.greaterThan(kerjasama.get("tahun"), 0);
        } else {
            tahunPredicate = cb.equal(kerjasama.get("tahun"), tahun);
        }

        Predicate searchPredicate = cb.like(kerjasama.get("mitraBusiness").get("nama"), searchTerm);

        JpaPredicate predicate = cb.and(tahunPredicate, searchPredicate);
       
        cq.where(predicate);

        cq.orderBy(
            cb.desc(kerjasama.get("kelompok")),
            cb.desc(kerjasama.get("tahun")),
            cb.desc(pemdaJoin.get("nama"))
        );

        cq.select(kerjasama);

        List<KerjaSamaPenyediaanPangan> kerjasamaList = entityManager.createQuery(cq)
                                                                    .setFirstResult(pageNo * pageSize)
                                                                    .setMaxResults(pageSize)
                                                                    .getResultList();

        TypedQuery<Long> countQuery = entityManager.createQuery(cq.createCountQuery());
        Long totalElements = countQuery.getSingleResult();

        Page<KerjaSamaPenyediaanPangan> kerjasamaPage = new Page<>(kerjasamaList);
        kerjasamaPage.setRecordsFiltered((int) totalElements.longValue());
        kerjasamaPage.setRecordsTotal((int) totalElements.longValue());
        kerjasamaPage.setDraw(pagingRequest.getDraw());

        return kerjasamaPage;
    }
}
