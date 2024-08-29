package id.go.jakarta.bksd.dokumen.domain;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import id.go.jakarta.bksd.dokumen.domain.datatables.Direction;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public final class PemakaiComparators {

    @EqualsAndHashCode
    @AllArgsConstructor
    @Getter
    static class Key {
        String name;
        Direction dir;
    }

    static Map<Key, Comparator<Pemakai>> map = new HashMap<>();

    static {
        map.put(new Key("title", Direction.asc), Comparator.comparing(Pemakai::getTitle));
        map.put(new Key("title", Direction.desc), Comparator.comparing(Pemakai::getTitle).reversed());

        map.put(new Key("jabatan", Direction.asc), Comparator.comparing(Pemakai::getJabatan));
        map.put(new Key("jabatan", Direction.desc), Comparator.comparing(Pemakai::getJabatan).reversed());

        map.put(new Key("namaDepan", Direction.asc), Comparator.comparing(Pemakai::getNamaDepan));
        map.put(new Key("namaDepan", Direction.desc), Comparator.comparing(Pemakai::getNamaDepan).reversed());

        map.put(new Key("namaKeluarga", Direction.asc), Comparator.comparing(Pemakai::getNamaKeluarga));
        map.put(new Key("namaKeluarga", Direction.desc), Comparator.comparing(Pemakai::getNamaKeluarga).reversed());

        map.put(new Key("email", Direction.asc), Comparator.comparing(Pemakai::getEmail));
        map.put(new Key("email", Direction.desc), Comparator.comparing(Pemakai::getEmail).reversed());

        map.put(new Key("createdDate", Direction.asc), Comparator.comparing(Pemakai::getCreatedDate));
        map.put(new Key("createdDate", Direction.desc), Comparator.comparing(Pemakai::getCreatedDate).reversed());

        map.put(new Key("modifiedDate", Direction.asc), Comparator.comparing(Pemakai::getModifiedDate));
        map.put(new Key("modifiedDate", Direction.desc), Comparator.comparing(Pemakai::getModifiedDate).reversed());

    }

    public static Comparator<Pemakai> getComparator(String name, Direction dir) {
        return map.get(new Key(name, dir));
    }

    private PemakaiComparators() {
    }
}