
    create sequence verification_token_seq start with 1 increment by 50 nocache;

    create table asisten_sekretaris_daerah (
        id bigint not null auto_increment,
        nama varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table bagian (
        id bigint not null auto_increment,
        nama varchar(255),
        singkatan varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table column_order (
        column_number integer,
        id bigint not null auto_increment,
        nama_table varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table dokumen (
        dokumenkerjasama_id bigint,
        id bigint not null auto_increment,
        filename varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table dokumen_kerja_sama (
        deleted bit not null,
        files_telah_diperoleh bit not null,
        tahun integer not null,
        bagian_id bigint not null,
        created_date datetime(6),
        id bigint not null auto_increment,
        jenis_dokumen_id bigint,
        jenis_mitra_id bigint,
        modified_date datetime(6),
        tgl_penetapan datetime(6),
        waktu_pelaksanaan_berakhir datetime(6),
        waktu_pelaksanaan_dimulai datetime(6),
        created_by varchar(255),
        file_content MEDIUMTEXT,
        jangka_waktu_pelaksanaan varchar(512),
        judul_dokumen_perjanjian varchar(512),
        keterangan varchar(2048),
        keterangan2 varchar(1024),
        keterangan3 varchar(1024),
        keterangan4 varchar(1024),
        link_dokumen_kerja_sama varchar(1024),
        mitra_kerja_sama varchar(1024),
        modified_by varchar(255),
        no_dokumen_perjanjian varchar(512),
        pembiayaan varchar(1024),
        urusan_pemerintahan_yg_dikerjakan varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table dokumen_kerja_sama_opd (
        dokumen_kerja_sama_id bigint not null,
        opd_id bigint not null
    ) engine=InnoDB;

    create table dokumen_kerja_sama_rumpun_opd (
        dokumen_kerja_sama_id bigint not null,
        rumpun_opd_id bigint not null
    ) engine=InnoDB;

    create table dokumen_kerja_sama_luar_negeri (
        deleted bit not null,
        tahun integer not null,
        created_date datetime(6),
        id bigint not null auto_increment,
        jenis_dokumen_id bigint,
        modified_date datetime(6),
        tgl datetime(6),
        tgl_berakhir datetime(6),
        tgl_dimulai datetime(6),
        created_by varchar(255),
        file_content MEDIUMTEXT,
        judul_dokumen varchar(2048),
        keterangan varchar(2048),
        link_dokumen varchar(2048),
        modified_by varchar(255),
        penanggung_jawab_tertanda_tangan varchar(2048),
        subjek varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table dokumen_luar_negeri (
        dokumen_kerja_sama_luar_negeri_id bigint,
        id bigint not null auto_increment,
        filename varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table jenis_mitra (
        id bigint not null auto_increment,
        kelompok varchar(255),
        nama varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table jenis_naskah (
        id bigint not null auto_increment,
        nama varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table jenis_naskah_luar_negeri (
        id bigint not null auto_increment,
        nama varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table kerja_sama_penyediaan_pangan (
        deleted bit not null,
        kuantitas float(53) not null,
        nilai_transaksi float(53) not null,
        tahun integer not null,
        bumd_id bigint,
        created_date datetime(6),
        id bigint not null auto_increment,
        jenis_id bigint,
        kelompok_id bigint,
        modified_date datetime(6),
        created_by varchar(255),
        keterangan varchar(255),
        mitra_business varchar(255),
        modified_by varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table kerja_sama_penyediaan_pangan_kota_kabupaten (
        kerja_sama_penyediaan_pangan_id bigint not null,
        kota_kabupaten_id bigint not null
    ) engine=InnoDB;

    create table kerja_sama_penyediaan_pangan_pemda (
        kerja_sama_penyediaan_pangan_id bigint not null,
        pemda_id bigint not null
    ) engine=InnoDB;

    create table kolaborasi (
        deleted bit not null,
        tahun integer not null,
        id bigint not null auto_increment,
        keterangan varchar(255),
        mitra_kerja_sama varchar(255),
        nama_dan_tempat_kejadian varchar(512),
        output varchar(1024),
        waktu varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table kolaborasi_bagian (
        bagian_id bigint not null,
        kolaborasi_id bigint not null
    ) engine=InnoDB;

    create table kspp_bumd (
        id bigint not null auto_increment,
        nama varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table kspp_jenis (
        id bigint not null auto_increment,
        nama varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table kspp_kelompok (
        id bigint not null auto_increment,
        nama varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table kspp_kota_kabupaten (
        id bigint not null auto_increment,
        nama varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table kspp_pemda (
        id bigint not null auto_increment,
        nama varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table opd (
        asisten_id bigint,
        id bigint not null auto_increment,
        nama varchar(255),
        singkatan varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table pemetaan (
        deleted bit not null,
        tahun integer not null,
        bagian_id bigint,
        created_date datetime(6),
        id bigint not null auto_increment,
        jenis_mitra_id bigint,
        modified_date datetime(6),
        opd_id bigint,
        created_by varchar(255),
        jangka_waktu varchar(255),
        kegiatan varchar(1024),
        modified_by varchar(255),
        nama_calon_mitra varchar(1024),
        no_kontrak_calon_mitra varchar(255),
        pic varchar(255),
        skala_prioritas varchar(255),
        sumber_pembiayaan varchar(255),
        urusan_pemerintahan varchar(255),
        waktu_pelaksanaan varchar(255),
        waktu_pelaksanaantw1 varchar(255),
        waktu_pelaksanaantw2 varchar(255),
        waktu_pelaksanaantw3 varchar(255),
        waktu_pelaksanaantw4 varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table pk_dokumen_pendukung_kolaborasi (
        id bigint not null auto_increment,
        filename varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table pk_jenis_kolaborasi (
        id bigint not null auto_increment,
        nama varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table pk_pengajuan_kolaborasi (
        created_date datetime(6),
        id bigint not null auto_increment,
        jenis_kolaborasi_id bigint,
        kolaborator_id bigint,
        modified_date datetime(6),
        photo_kolaborasi_id bigint,
        profilepic_id bigint,
        status_pengajuan_id bigint,
        subjek_kerja_sama_id bigint,
        created_by varchar(255),
        deskripsi_kolaborasi varchar(255),
        estimasi_anggaran varchar(255),
        modified_by varchar(255),
        periode_kegiatan varchar(255),
        pernyataan varchar(255),
        sasaran varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table pk_pengajuan_kolaborasi_dokumen_pendukung_kolaborasi (
        dokumen_pendukung_kolaborasi_id bigint not null,
        pengajuan_kolaborasi_id bigint not null
    ) engine=InnoDB;

    create table pk_pengajuan_kolaborasi_mitra_kolaborator (
        mitra_kolaborator_id bigint not null,
        pengajuan_kolaborasi_id bigint not null
    ) engine=InnoDB;

    create table pk_photo_kolaborasi (
        id bigint not null auto_increment,
        banner varchar(255),
        thumbnail varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table pk_profile_pic (
        id bigint not null auto_increment,
        email varchar(255),
        nama_lengkap varchar(255),
        no_telephone varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table pk_status_pengajuan_kolaborasi (
        id bigint not null auto_increment,
        nama varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table pk_subjek_kerja_sama (
        id bigint not null auto_increment,
        nama varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table privilege (
        id bigint not null auto_increment,
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table role (
        id bigint not null auto_increment,
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table roles_privileges (
        privilege_id bigint not null,
        role_id bigint not null
    ) engine=InnoDB;

    create table table_definition (
        id bigint not null auto_increment,
        nama_column varchar(255),
        nama_table varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table user (
        enabled bit not null,
        created_date datetime(6),
        id bigint not null auto_increment,
        modified_date datetime(6),
        created_by varchar(255),
        email varchar(255),
        jabatan varchar(255),
        modified_by varchar(255),
        nama_depan varchar(255),
        nama_keluarga varchar(255),
        password varchar(255),
        profile_image varbinary(255),
        reset_str varchar(255),
        title varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table user_column_order (
        column_order_id bigint not null,
        pemakai_id bigint not null
    ) engine=InnoDB;

    create table user_table_definition (
        pemakai_id bigint not null,
        table_definition_id bigint not null
    ) engine=InnoDB;

    create table user_activity (
        id bigint not null auto_increment,
        pemakai_id bigint,
        timestamp datetime(6),
        arguments varchar(8092),
        method_name varchar(255),
        target_class varchar(255),
        username varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table users_roles (
        role_id bigint not null,
        user_id bigint not null
    ) engine=InnoDB;

    create table verification_token (
        expiry_date datetime(6),
        id bigint not null,
        pemakai_id bigint not null,
        token varchar(255),
        primary key (id)
    ) engine=InnoDB;

    alter table if exists kspp_jenis 
       add constraint UKkbq1gofxfo0aex3a3mc6o4b77 unique (nama);

    alter table if exists pk_pengajuan_kolaborasi 
       add constraint UKkrm0kjpnhv0biarm3s8oqoav3 unique (photo_kolaborasi_id);

    alter table if exists pk_pengajuan_kolaborasi 
       add constraint UK8cbs90p4x3ynfweln9de47m91 unique (profilepic_id);

    alter table if exists pk_pengajuan_kolaborasi 
       add constraint UKdcb93qsqaivpjoejghjnuy8dx unique (subjek_kerja_sama_id);

    alter table if exists pk_pengajuan_kolaborasi_dokumen_pendukung_kolaborasi 
       add constraint UKp2td4o0u22leycd8gdo7b1gvx unique (dokumen_pendukung_kolaborasi_id);

    alter table if exists verification_token 
       add constraint UKos7m7pyvweq39bblim6fbkrto unique (pemakai_id);

    alter table if exists dokumen 
       add constraint FKcfagfjlqgh2kr0ehasn6pvjm0 
       foreign key (dokumenkerjasama_id) 
       references dokumen_kerja_sama (id);

    alter table if exists dokumen_kerja_sama 
       add constraint FKmv70j6vs868v9vufm0a4sbhu2 
       foreign key (bagian_id) 
       references bagian (id);

    alter table if exists dokumen_kerja_sama 
       add constraint FK72l83uvu6h9ti07julj3d8h8 
       foreign key (jenis_dokumen_id) 
       references jenis_naskah (id);

    alter table if exists dokumen_kerja_sama 
       add constraint FK86g9mbi30hpjykpx3h91yfb4d 
       foreign key (jenis_mitra_id) 
       references jenis_mitra (id);

    alter table if exists dokumen_kerja_sama_opd 
       add constraint FK5x9mwcd9uoohqneea9qol4oqj 
       foreign key (opd_id) 
       references opd (id);

    alter table if exists dokumen_kerja_sama_opd 
       add constraint FKa9n8ugiqdg6o9gx34n9fwu6ci 
       foreign key (dokumen_kerja_sama_id) 
       references dokumen_kerja_sama (id);

    alter table if exists dokumen_kerja_sama_rumpun_opd 
       add constraint FKg4ww1uuljl5t46b8ieywjug1r 
       foreign key (rumpun_opd_id) 
       references asisten_sekretaris_daerah (id);

    alter table if exists dokumen_kerja_sama_rumpun_opd 
       add constraint FKhqvnyfmn53l1apyh2eso5ovgl 
       foreign key (dokumen_kerja_sama_id) 
       references dokumen_kerja_sama (id);

    alter table if exists dokumen_kerja_sama_luar_negeri 
       add constraint FK5254459jgqhq4scoitmvwgyy2 
       foreign key (jenis_dokumen_id) 
       references jenis_naskah_luar_negeri (id);

    alter table if exists dokumen_luar_negeri 
       add constraint FKnoe4bcxsnwl3iv6yymb72hsw1 
       foreign key (dokumen_kerja_sama_luar_negeri_id) 
       references dokumen_kerja_sama_luar_negeri (id);

    alter table if exists kerja_sama_penyediaan_pangan 
       add constraint FK54d3wdu9e016j1wq9llusk8wr 
       foreign key (bumd_id) 
       references kspp_bumd (id);

    alter table if exists kerja_sama_penyediaan_pangan 
       add constraint FKgxtyp1iakasbkypuu6o6iuay5 
       foreign key (jenis_id) 
       references kspp_jenis (id);

    alter table if exists kerja_sama_penyediaan_pangan 
       add constraint FK34jhdrhp8tdx3fuad336pd0dd 
       foreign key (kelompok_id) 
       references kspp_kelompok (id);

    alter table if exists kerja_sama_penyediaan_pangan_kota_kabupaten 
       add constraint FK1h4877odc7hv4y4ji24loxtxb 
       foreign key (kota_kabupaten_id) 
       references kspp_kota_kabupaten (id);

    alter table if exists kerja_sama_penyediaan_pangan_kota_kabupaten 
       add constraint FKnbvp7slw27wgkwj6ou6y42lgr 
       foreign key (kerja_sama_penyediaan_pangan_id) 
       references kerja_sama_penyediaan_pangan (id);

    alter table if exists kerja_sama_penyediaan_pangan_pemda 
       add constraint FK5gf6fd1mvl9xbqgqd2bs0r4o9 
       foreign key (pemda_id) 
       references kspp_pemda (id);

    alter table if exists kerja_sama_penyediaan_pangan_pemda 
       add constraint FK6plttljuiktl3kv25i56178hl 
       foreign key (kerja_sama_penyediaan_pangan_id) 
       references kerja_sama_penyediaan_pangan (id);

    alter table if exists kolaborasi_bagian 
       add constraint FKkytop5umbvf62qexhxj3e0i9p 
       foreign key (bagian_id) 
       references bagian (id);

    alter table if exists kolaborasi_bagian 
       add constraint FKi5y8tpwgq6ult2o4hlf7sbvpv 
       foreign key (kolaborasi_id) 
       references kolaborasi (id);

    alter table if exists opd 
       add constraint FKanbmhox8n88nllcnpt3twcc40 
       foreign key (asisten_id) 
       references asisten_sekretaris_daerah (id);

    alter table if exists pemetaan 
       add constraint FKm118nv56rynckbe5ww1dlyxp8 
       foreign key (bagian_id) 
       references bagian (id);

    alter table if exists pemetaan 
       add constraint FKgekwxuhv95sdh6gew7avr4mef 
       foreign key (jenis_mitra_id) 
       references jenis_mitra (id);

    alter table if exists pemetaan 
       add constraint FK2jj7utic8kqibmsdweri79nx2 
       foreign key (opd_id) 
       references opd (id);

    alter table if exists pk_pengajuan_kolaborasi 
       add constraint FKo0qahlp1l7tn9dmsjuyhc2t1b 
       foreign key (jenis_kolaborasi_id) 
       references pk_jenis_kolaborasi (id);

    alter table if exists pk_pengajuan_kolaborasi 
       add constraint FK7oweep1pbc2yg017x4mntny9i 
       foreign key (kolaborator_id) 
       references user (id);

    alter table if exists pk_pengajuan_kolaborasi 
       add constraint FKijglwe0a11k6o80pvp2qxc56j 
       foreign key (photo_kolaborasi_id) 
       references pk_photo_kolaborasi (id);

    alter table if exists pk_pengajuan_kolaborasi 
       add constraint FK7pkw0if9lsc1anwgivsd2adp0 
       foreign key (profilepic_id) 
       references pk_profile_pic (id);

    alter table if exists pk_pengajuan_kolaborasi 
       add constraint FKfntfvg9uyhdn8u82c0a4439tu 
       foreign key (status_pengajuan_id) 
       references pk_status_pengajuan_kolaborasi (id);

    alter table if exists pk_pengajuan_kolaborasi 
       add constraint FK3p02p1qgu1yq25v9ajymcrg79 
       foreign key (subjek_kerja_sama_id) 
       references pk_subjek_kerja_sama (id);

    alter table if exists pk_pengajuan_kolaborasi_dokumen_pendukung_kolaborasi 
       add constraint FKg2357gg64x764d2csdotaealv 
       foreign key (dokumen_pendukung_kolaborasi_id) 
       references pk_dokumen_pendukung_kolaborasi (id);

    alter table if exists pk_pengajuan_kolaborasi_dokumen_pendukung_kolaborasi 
       add constraint FKctvcc0vydtysjcq24hkpf6u19 
       foreign key (pengajuan_kolaborasi_id) 
       references pk_pengajuan_kolaborasi (id);

    alter table if exists pk_pengajuan_kolaborasi_mitra_kolaborator 
       add constraint FKadtgcjh4q5i4o1ah13tvcel9g 
       foreign key (mitra_kolaborator_id) 
       references user (id);

    alter table if exists pk_pengajuan_kolaborasi_mitra_kolaborator 
       add constraint FK9bkwfeh9sqalm5jbqfo6rwonb 
       foreign key (pengajuan_kolaborasi_id) 
       references pk_pengajuan_kolaborasi (id);

    alter table if exists roles_privileges 
       add constraint FK5yjwxw2gvfyu76j3rgqwo685u 
       foreign key (privilege_id) 
       references privilege (id);

    alter table if exists roles_privileges 
       add constraint FK9h2vewsqh8luhfq71xokh4who 
       foreign key (role_id) 
       references role (id);

    alter table if exists user_column_order 
       add constraint FKthumadyrsmm2awh1ricelncqb 
       foreign key (column_order_id) 
       references column_order (id);

    alter table if exists user_column_order 
       add constraint FKba5o0y7ldbfcyslieft80xg1g 
       foreign key (pemakai_id) 
       references user (id);

    alter table if exists user_table_definition 
       add constraint FK55hfy0f284fi1sq39roikrce0 
       foreign key (table_definition_id) 
       references table_definition (id);

    alter table if exists user_table_definition 
       add constraint FKkq15heuta3q6bxbcy98un5u8q 
       foreign key (pemakai_id) 
       references user (id);

    alter table if exists user_activity 
       add constraint FKl4eihy4k2jfxthhc66yiq22mq 
       foreign key (pemakai_id) 
       references user (id);

    alter table if exists users_roles 
       add constraint FKt4v0rrweyk393bdgt107vdx0x 
       foreign key (role_id) 
       references role (id);

    alter table if exists users_roles 
       add constraint FKgd3iendaoyh04b95ykqise6qh 
       foreign key (user_id) 
       references user (id);

    alter table if exists verification_token 
       add constraint FKt5vmb8cenvy8erfm5tm60r5ad 
       foreign key (pemakai_id) 
       references user (id);
