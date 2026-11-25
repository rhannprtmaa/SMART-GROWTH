# Task Breakdown — Implementasi PRD Daftar Hadir Posyandu & Pengukuran

**Sumber:** [`PRD.md`](./PRD.md) v1.0 (Final)
**Versi:** 1.0
**Tanggal:** 25 November 2026
**Status:** Rencana implementasi — belum ada kode yang diubah
**Target MVP:** Android Parent + Kader, satu Posyandu Bontoulu, WITA.

---

## 1. Ringkasan

Dokumen ini memecah PRD menjadi **7 epic**, **4 sprint**, dan **57 task** yang bisa dieksekusi.

| Epic | Fokus | Sprint | Estimasi |
|---|---|---|---|
| FND | Fondasi, audit skema, perbaikan blocker | Sprint 0 | 1 minggu |
| DB | Migrasi database, RLS, RPC | Sprint 0 | 1 minggu |
| DATA | Model, service, ViewModel Android | Sprint 1 | 1 minggu |
| PAR | Alur orang tua | Sprint 1 | 2 minggu |
| KDR | Alur kader dan pengukuran | Sprint 2 | 2–3 minggu |
| IMP | Import data lama | Sprint 3 | 1 minggu |
| QA | Pengujian, regresi web, rilis | Sprint 3 | 1–2 minggu |

**Total perkiraan:** 6–8 minggu untuk 1 developer Android dengan dukungan SQL/backend paruh waktu.

**Critical path:**

```
FND-00 → FND-01 → FND-02 (fondasi + audit skema)
  → DB-01..DB-08 (migrasi + RLS + RPC)
  → DATA-01..DATA-05 (model + service + ViewModel)
  → KDR-01..KDR-08 (tanggal, daftar hadir, validasi, ukur, walk-in)
  → IMP-01..IMP-05 (import)
  → QA-01..QA-08 (uji + rilis)
```

Epic PAR bisa berjalan paralel setelah DATA-01..DATA-05 selesai. Jangan mulai UI produksi sebelum DB-06 dan DB-07 lulus uji di staging.

---

## 2. Aturan Kerja

### 2.1 Prioritas

| Kode | Arti |
|---|---|
| **P0** | Wajib untuk MVP; menghambat alur utama. |
| **P1** | Wajib untuk kualitas MVP; boleh menyusul setelah alur utama jalan. |
| **P2** | Fase 2; jangan dikerjakan di MVP. |

### 2.2 Estimasi

| Kode | Arti |
|---|---|
| **S** | ≤ 1 hari kerja. |
| **M** | 2–3 hari kerja. |
| **L** | 4–5 hari kerja. |

Estimasi bersifat relatif, bukan commitment tanggal.

### 2.3 Definition of Done

Sebuah task dianggap selesai jika:

- [ ] Kode compile dan tidak menambah warning baru.
- [ ] Acceptance criteria task terpenuhi.
- [ ] Loading, empty state, dan error state ditangani.
- [ ] Tidak ada password, token, NIK lengkap, atau data pribadi di log.
- [ ] Task database diuji di staging dan memiliki rollback note.
- [ ] Task yang mengubah data diuji dengan dua akun Parent berbeda.
- [ ] Task UI memiliki minimal satu unit test atau UI test untuk logika kritis.
- [ ] Dokumentasi terkait diperbarui.

### 2.4 Aturan branch dan commit

- Branch fitur: `feat/posyandu-attendance`.
- Commit kecil per task atau per kelompok task yang saling terkait.
- Format commit: `feat(attendance): ...`, `fix(child): ...`, `db(attendance): ...`, `test(attendance): ...`, `docs: ...`.
- Jangan mencampur perubahan `.idea/`, `gradle.properties`, atau file pribadi ke commit fitur.

### 2.5 Prinsip teknis

- Ikuti pola yang sudah ada: `Service` → `ViewModel` → `Screen` Compose, tanpa menambah framework baru.
- Gunakan `UiState` yang sudah ada.
- Gunakan `@Serializable` dan `@SerialName` untuk DTO.
- Semua operasi tulis multi-tabel lewat RPC transaksional.
- RLS adalah pengaman utama; UI bukan pengaman.
- Tanggal memakai `Asia/Makassar`, bukan jam perangkat.

---

## 3. Rencana Sprint

### Sprint 0 — Fondasi & Database

**Tujuan:** skema aman, RLS terbukti, RPC siap, blocker UI dibereskan.

**Exit criteria:**

- [ ] Baseline skema tersimpan di repo.
- [ ] Migrasi berjalan di staging dari kondisi existing.
- [ ] RLS lulus uji dua Parent.
- [ ] RPC utama lulus uji manual.
- [ ] `AppTopBar` dan route menu ganda diperbaiki.
- [ ] Tidak ada password di log.

### Sprint 1 — Data Layer & Parent

**Tujuan:** orang tua bisa melihat tanggal aktif, mendaftarkan banyak anak, membatalkan, dan melihat status.

**Exit criteria:**

- [ ] Parent hanya melihat anak sendiri.
- [ ] Parent bisa daftar 1–3 anak sekaligus.
- [ ] Submit ulang tidak membuat duplikat.
- [ ] Parent bisa batal sebelum diukur.
- [ ] Empty state saat belum ada tanggal.
- [ ] Banner tanggal aktif muncul di Home Parent.

### Sprint 2 — Kader & Pengukuran

**Tujuan:** kader bisa membuka tanggal, melihat daftar, memvalidasi, mengukur, walk-in, dan menutup pendaftaran.

**Exit criteria:**

- [ ] Kader bisa buka/tutup/buka kembali tanggal.
- [ ] Tanggal terkunci setelah ada entri.
- [ ] Validasi inline sebelum ukur.
- [ ] BB/TB wajib; LK/LILA opsional.
- [ ] Koreksi pengukuran tanggal sama tidak membuat baris baru.
- [ ] Walk-in membuat entri daftar hadir dulu.
- [ ] Tidak ada akses ukur dari profil anak.

### Sprint 3 — Import, QA, Release

**Tujuan:** data lama masuk dengan benar, seluruh AC lulus, aplikasi siap dipakai kader.

**Exit criteria:**

- [ ] Import akun, anak, dan pengukuran selesai di staging.
- [ ] Import ulang tidak membuat duplikat.
- [ ] Laporan import direview.
- [ ] AC-01..AC-27 lulus.
- [ ] Dashboard/ekspor web tidak rusak.
- [ ] Release build siap distribusi.
- [ ] Kader mendapat panduan singkat.

---

## 4. Detail Task

## EPIC 0 — Fondasi (FND)

### FND-00 — Bersihkan working tree dan buat branch fitur

- **Priority:** P0
- **Est:** S
- **Depends:** —
- **Files:** —
- **Scope:**
  - Commit atau stash perubahan yang belum terkait: `AuthViewModel.kt`, `gradle.properties`, `libs.versions.toml`, `gradle-wrapper.properties`, file `.idea`.
  - Buat branch `feat/posyandu-attendance`.
- **Acceptance:**
  - [ ] `git status` bersih dari perubahan yang tidak terkait.
  - [ ] Branch fitur aktif.
  - [ ] PRD dan task breakdown sudah ada di branch.

### FND-01 — Audit skema Supabase dan simpan baseline

- **Priority:** P0
- **Est:** M
- **Depends:** FND-00
- **Files:** `supabase/schema.sql`, `docs/SCHEMA.md`
- **Scope:**
  - Ekspor skema dari Supabase (tabel, kolom, tipe, constraint, index, RLS policy, function, trigger).
  - Konfirmasi nilai `users.role` (`Parent`, `Admin`, `Super Admin`).
  - Konfirmasi kolom nyata di `children` (termasuk `child_no`, `armCm`) dan `measurements`.
  - Catat policy RLS yang sudah ada dan potensi konflik.
  - Buat backup database sebelum perubahan.
- **Acceptance:**
  - [ ] `supabase/schema.sql` committed.
  - [ ] `docs/SCHEMA.md` memuat daftar tabel dan kolom aktual.
  - [ ] Perbedaan antara kode Android dan skema nyata dicatat.
  - [ ] Backup tersedia dan bisa direstore.
- **Catatan:** tanpa task ini, migrasi berisiko gagal karena nama kolom camelCase dan constraint lama tidak terdokumentasi.

### FND-02 — Siapkan workflow migrasi, staging, dan rollback

- **Priority:** P0
- **Est:** M
- **Depends:** FND-01
- **Files:** `supabase/README.md`, `supabase/migrations/`
- **Scope:**
  - Tentukan cara menjalankan migrasi: Supabase CLI atau SQL Editor.
  - Siapkan project/staging database.
  - Buat template migrasi bernomor tanggal.
  - Tulis prosedur rollback per migrasi.
- **Acceptance:**
  - [ ] Migrasi contoh berjalan di staging.
  - [ ] Rollback contoh berhasil.
  - [ ] Prosedur terdokumentasi di `supabase/README.md`.

### FND-03 — Perbaiki `AppTopBar` dan route menu ganda

- **Priority:** P0
- **Est:** S
- **Depends:** FND-00
- **Files:** `ui/components/AppTopBar.kt`, `data/model/MenuItem.kt`
- **Scope:**
  - Isi blok `if (showSearch) { }` atau hapus parameter jika tidak dipakai.
  - Pastikan aksi search di `AddChildrenScreen` dan layar lain berfungsi.
  - Pisahkan route "Perjalanan Pertumbuhan" dari "Statistik Pertumbuhan".
- **Acceptance:**
  - [ ] Tombol search membuka mode pencarian.
  - [ ] Tombol simpan pada layar tambah anak tidak lagi disabled karena bug ini.
  - [ ] Dua menu membuka layar berbeda.
  - [ ] Tidak ada regresi pada layar yang memakai `AppTopBar`.

### FND-04 — Hapus logging password dan data pribadi

- **Priority:** P0
- **Est:** S
- **Depends:** FND-00
- **Files:** `ui/viewmodel/AuthViewModel.kt`, `data/supabase/AuthService.kt`, `data/supabase/UserService.kt`
- **Scope:**
  - Hapus `Log.d` yang mencetak `InsertUserDto` atau password.
  - Hapus logging access token.
  - Samarkan NIK/email di log debug jika masih diperlukan.
- **Acceptance:**
  - [ ] Tidak ada password/token di Logcat saat register dan login.
  - [ ] Tidak ada NIK lengkap di log.
  - [ ] Pencarian teks `password` di folder `data` dan `viewmodel` tidak menemukan log sensitif.

### FND-05 — Siapkan test scaffolding dan build check

- **Priority:** P1
- **Est:** S
- **Depends:** FND-00
- **Files:** `app/build.gradle.kts`, `app/src/test/`, `app/src/androidTest/`
- **Scope:**
  - Pastikan `./gradlew test` dan `./gradlew assembleDebug` berjalan.
  - Tambahkan dependensi test yang diperlukan (coroutines-test, Compose UI test sudah ada).
  - Buat satu unit test contoh untuk util tanggal.
- **Acceptance:**
  - [ ] `./gradlew test` lulus.
  - [ ] `./gradlew assembleDebug` lulus.
  - [ ] Minimal satu unit test berjalan di CI lokal.

---

## EPIC 1 — Database, RLS, dan RPC (DB)

### DB-01 — Buat tabel `posyandu_dates`

- **Priority:** P0
- **Est:** S
- **Depends:** FND-02
- **Files:** `supabase/migrations/*_posyandu_dates.sql`
- **Scope:**
  - Kolom: `id`, `posyandu_date`, `status`, `opened_by`, `opened_at`, `closed_by`, `closed_at`, `created_at`, `updated_at`.
  - `UNIQUE (posyandu_date)`.
  - `CHECK (status IN ('open','closed'))`.
  - Partial unique index satu tanggal `open`.
- **Acceptance:**
  - [ ] Insert dua tanggal `open` ditolak.
  - [ ] Insert tanggal yang sama dua kali ditolak.
  - [ ] Tanggal `closed` tidak menghalangi pembukaan tanggal lain.
  - [ ] Migrasi dan rollback terdokumentasi.

### DB-02 — Buat tabel `attendance_items`

- **Priority:** P0
- **Est:** S
- **Depends:** DB-01
- **Files:** `supabase/migrations/*_attendance_items.sql`
- **Scope:**
  - Kolom: `id`, `attendance_date`, `child_id`, `status`, `source`, `measurement_id`, `created_at`, `updated_at`.
  - FK ke `posyandu_dates(posyandu_date)` dengan `ON UPDATE CASCADE ON DELETE CASCADE`.
  - FK ke `children(id)` dan `measurements(id)`.
  - `UNIQUE (attendance_date, child_id)`.
  - Check status dan source.
- **Acceptance:**
  - [ ] Satu anak hanya bisa satu entri per tanggal.
  - [ ] Entri tanpa tanggal valid ditolak.
  - [ ] Delete tanggal ikut menghapus entri (sesuai aturan).
  - [ ] Index `(attendance_date, status)` dan `(child_id)` dibuat.

### DB-03 — Tambah kolom validasi di `children` dan backfill

- **Priority:** P0
- **Est:** S
- **Depends:** FND-02
- **Files:** `supabase/migrations/*_children_validation.sql`
- **Scope:**
  - Tambah `validation_status`, `verified_by`, `verified_at`, `verification_note`.
  - Default `validation_status = 'pending'`.
  - Backfill semua anak existing menjadi `verified`, `verified_at = coalesce(created_at, now())`, `verification_note = 'migrasi'`.
  - Tambah check status.
- **Acceptance:**
  - [ ] Anak existing berstatus `verified`.
  - [ ] Anak baru dari Parent berstatus `pending`.
  - [ ] Kader bisa mengubah status lewat RPC.
  - [ ] Rollback mengembalikan kolom lama tanpa kehilangan data.

### DB-04 — Tambah metadata `measurements`, dedupe, dan backfill

- **Priority:** P0
- **Est:** M
- **Depends:** FND-02
- **Files:** `supabase/migrations/*_measurements_metadata.sql`
- **Scope:**
  - Tambah `measure_date`, `source`, `device_id`, `measured_by`, `updated_at`, `updated_by`.
  - Backfill `measure_date` dari `measured_at` atau `created_at` pada zona WITA.
  - Set `source = 'migration'` untuk data lama.
  - Deteksi duplikat `(children_id, measure_date)` sebelum unique constraint.
  - Simpan baris terbaru, laporkan baris yang dihapus/dilewati.
  - Buat `UNIQUE (children_id, measure_date)`.
- **Acceptance:**
  - [ ] Semua baris lama punya `measure_date`.
  - [ ] Tidak ada duplikat setelah migrasi.
  - [ ] Constraint menolak pengukuran kedua pada tanggal yang sama.
  - [ ] Laporan duplikat tersedia.
- **Catatan:** jangan hapus data sebelum backup dan review laporan.

### DB-05 — Index, constraint, dan zona waktu WITA

- **Priority:** P0
- **Est:** S
- **Depends:** DB-01..DB-04
- **Files:** `supabase/migrations/*_indexes_timezone.sql`
- **Scope:**
  - Index untuk `posyandu_dates`, `attendance_items`, `measurements`, `children`.
  - Set zona waktu database ke `Asia/Makassar`.
  - Verifikasi `current_date` pada simulasi pukul 00:30 WITA.
  - Pastikan default `measure_date` memakai tanggal WITA.
- **Acceptance:**
  - [ ] `SHOW timezone` menghasilkan `Asia/Makassar`.
  - [ ] `current_date` benar pada batas tengah malam WITA.
  - [ ] Query daftar hadir memakai index.

### DB-06 — Helper function dan RLS semua tabel

- **Priority:** P0
- **Est:** L
- **Depends:** DB-01..DB-05
- **Files:** `supabase/migrations/*_rls.sql`
- **Scope:**
  - Buat helper `is_kader()` dan `is_parent()`.
  - Aktifkan RLS di `posyandu_dates`, `attendance_items`, `children`, `measurements`, `users`.
  - Parent:
    - SELECT anak sendiri.
    - SELECT tanggal `open` atau tanggal yang diikuti.
    - SELECT daftar hadir sendiri.
    - SELECT pengukuran anak sendiri.
    - Tidak boleh INSERT/UPDATE `measurements`.
    - Tidak boleh mengubah kolom validasi.
  - Kader:
    - SELECT semua anak dan daftar hadir.
    - INSERT/UPDATE tanggal, daftar hadir, pengukuran.
  - Pastikan service role tetap bisa dipakai untuk import (bypass RLS).
- **Acceptance:**
  - [ ] Parent A tidak bisa melihat anak Parent B.
  - [ ] Parent tidak bisa insert measurement.
  - [ ] Parent tidak bisa membuka/menutup tanggal.
  - [ ] Parent tidak bisa mengubah `validation_status`.
  - [ ] Kader bisa mengelola data.
  - [ ] Policy lama yang konflik sudah dihapus/diganti.

### DB-07 — RPC tanggal dan daftar hadir

- **Priority:** P0
- **Est:** L
- **Depends:** DB-06
- **Files:** `supabase/migrations/*_rpc_attendance.sql`
- **Scope:**
  - `open_posyandu_date(p_date)`.
  - `update_posyandu_date(p_id, p_new_date)`.
  - `close_posyandu_date(p_id)`.
  - `reopen_posyandu_date(p_id)`.
  - `register_attendance(p_date, p_child_ids[])`.
  - `cancel_attendance(p_date, p_child_id)`.
  - Validasi role, status tanggal, tanggal lampau, duplikat, dan kepemilikan anak.
- **Acceptance:**
  - [ ] Hanya kader yang bisa buka/tutup/ubah tanggal.
  - [ ] Parent hanya bisa daftar anak sendiri.
  - [ ] Parent tidak bisa daftar saat tanggal `closed` atau sudah lewat.
  - [ ] Submit ulang tidak membuat duplikat.
  - [ ] Error mengembalikan pesan yang bisa ditampilkan ke UI.

### DB-08 — RPC validasi, pengukuran, dan walk-in

- **Priority:** P0
- **Est:** L
- **Depends:** DB-07
- **Files:** `supabase/migrations/*_rpc_measurement.sql`
- **Scope:**
  - `verify_child(...)` dan `reject_child(...)`.
  - `add_walk_in(p_date, p_child_id)`.
  - `save_measurement(p_date, p_child_id, p_weight, p_height, p_head, p_arm, p_source, p_device_id)`.
  - `save_measurement` harus:
    - Cek kader.
    - Cek tanggal `open` dan `<= current_date`.
    - Cek entri daftar hadir ada.
    - Insert atau update berdasarkan `(children_id, measure_date)`.
    - Update `attendance_items.status = 'measured'` dan `measurement_id`.
    - Atomik dalam satu transaksi.
  - Walk-in membuat entri `source = 'kader'` terlebih dahulu.
- **Acceptance:**
  - [ ] Pengukuran tanpa entri daftar hadir ditolak.
  - [ ] Pengukuran sebelum tanggal posyandu ditolak.
  - [ ] Pengukuran pada tanggal `closed` ditolak.
  - [ ] Pengukuran kedua pada tanggal sama masuk mode koreksi, bukan baris baru.
  - [ ] Walk-in ganda untuk anak yang sudah terdaftar ditolak.
  - [ ] Status entri berubah menjadi `measured`.

### DB-09 — Seed data dan SQL test suite

- **Priority:** P1
- **Est:** M
- **Depends:** DB-08
- **Files:** `supabase/seed.sql`, `supabase/tests/*.sql`
- **Scope:**
  - Seed dua Parent, satu Kader, tiga anak, satu tanggal `open`, satu tanggal `closed`.
  - Test SQL untuk:
    - Satu tanggal open.
    - Duplikat daftar hadir.
    - Duplikat pengukuran.
    - Parent isolation.
    - Parent tidak bisa menulis measurement.
    - Tanggal closed menolak pendaftaran/pengukuran.
- **Acceptance:**
  - [ ] Seed bisa dijalankan ulang tanpa duplikat.
  - [ ] Semua SQL test lulus.
  - [ ] Hasil test terdokumentasi.

### DB-10 — Verifikasi kontrak web

- **Priority:** P1
- **Est:** S
- **Depends:** DB-04
- **Files:** `docs/WEB-CONTRACT.md`
- **Scope:**
  - Pastikan kolom lama `measurements` tetap terisi.
  - Pastikan kolom baru nullable/default.
  - Pastikan data `source = 'migration'` tetap terbaca dashboard/ekspor.
  - Catat query web yang perlu diuji.
- **Acceptance:**
  - [ ] Dashboard web membaca pengukuran baru.
  - [ ] Ekspor puskesmas tidak error.
  - [ ] Tidak ada perubahan role yang memutus web.

---

## EPIC 2 — Data Layer Android (DATA)

### DATA-01 — Model dan DTO baru

- **Priority:** P0
- **Est:** S
- **Depends:** DB-08
- **Files:** `data/model/Posyandu.kt`, `data/dto/PosyanduDto.kt`
- **Scope:**
  - `PosyanduDate`, `AttendanceItem`, `AttendanceRow`, `ChildValidation`, `Measurement`.
  - `@SerialName` sesuai kolom Supabase.
  - Status sebagai enum atau sealed string yang aman.
- **Acceptance:**
  - [ ] Semua field yang dipakai UI tersedia.
  - [ ] Deserialisasi data contoh berhasil.
  - [ ] Tidak ada nama kolom yang salah.

### DATA-02 — Service Supabase dan error mapping

- **Priority:** P0
- **Est:** L
- **Depends:** DATA-01
- **Files:** `data/supabase/PosyanduService.kt`, `data/supabase/AttendanceService.kt`, `data/supabase/MeasurementService.kt`
- **Scope:**
  - Query tanggal aktif dan tanggal terakhir Parent.
  - Query daftar hadir per tanggal dengan join anak dan orang tua.
  - Panggil RPC: open/update/close/reopen, register/cancel, verify/reject, walk-in, save measurement.
  - Map error Postgrest ke pesan Indonesia.
- **Acceptance:**
  - [ ] Semua operasi mengembalikan `Result<T>`.
  - [ ] Duplikat, tanggal tutup, dan akses ditolak menghasilkan pesan jelas.
  - [ ] Tidak ada query yang mengambil semua anak untuk Parent.

### DATA-03 — `PosyanduViewModel`

- **Priority:** P0
- **Est:** M
- **Depends:** DATA-02
- **Files:** `ui/viewmodel/PosyanduViewModel.kt`
- **Scope:**
  - State tanggal aktif, tanggal terakhir, daftar hadir Parent, daftar hadir Kader, ringkasan.
  - Aksi buka/tutup/buka kembali/ubah tanggal.
  - Aksi daftar/batal/walk-in.
  - Refresh setelah aksi.
- **Acceptance:**
  - [ ] State loading/empty/error konsisten.
  - [ ] Aksi berhasil memicu refresh.
  - [ ] Tidak ada state yang bocor antar-role.

### DATA-04 — `MeasurementViewModel`

- **Priority:** P0
- **Est:** M
- **Depends:** DATA-02
- **Files:** `ui/viewmodel/MeasurementViewModel.kt`
- **Scope:**
  - State form BB/TB/LK/LILA.
  - Validasi range dan field wajib.
  - Deteksi mode koreksi.
  - Simpan lewat RPC.
- **Acceptance:**
  - [ ] BB/TB kosong ditolak.
  - [ ] LK/LILA kosong diterima.
  - [ ] Nilai di luar rentang ditolak.
  - [ ] Mode koreksi memuat data lama.

### DATA-05 — Util tanggal WITA

- **Priority:** P0
- **Est:** S
- **Depends:** —
- **Files:** `data/utils/WitaDate.kt`
- **Scope:**
  - `todayWita()`, `formatTanggal()`, `parseTanggal()`.
  - Gunakan `ZoneId.of("Asia/Makassar")`.
  - Jangan memakai `LocalDate.now()` tanpa zone.
- **Acceptance:**
  - [ ] Unit test batas tengah malam WITA lulus.
  - [ ] Format tanggal konsisten di UI.
  - [ ] Tidak ada pemakaian jam perangkat untuk keputusan tanggal.

### DATA-06 — Refactor query anak dan pengukuran agar scoped

- **Priority:** P0
- **Est:** M
- **Depends:** DB-06
- **Files:** `data/supabase/ChildService.kt`, `ui/viewmodel/ChildViewModel.kt`
- **Scope:**
  - Tambah query anak berdasarkan Parent.
  - Hapus pemakaian `getAllChild()` untuk Parent.
  - Pastikan `getListMeasure()` tidak mengembalikan anak orang lain.
  - Pertahankan query global hanya untuk Kader.
- **Acceptance:**
  - [ ] Parent hanya menerima anak sendiri.
  - [ ] Statistik dan riwayat memakai data scoped.
  - [ ] Kader tetap bisa melihat semua anak.

### DATA-07 — Unit test aturan bisnis

- **Priority:** P1
- **Est:** M
- **Depends:** DATA-03..DATA-05
- **Files:** `app/src/test/...`
- **Scope:**
  - Status tanggal: open/closed, tanggal lampau.
  - Status validasi: pending/verified/rejected.
  - Status daftar hadir: registered/measured/canceled.
  - Validasi measurement.
- **Acceptance:**
  - [ ] Test mencakup BR-02, BR-03, BR-05, BR-12, BR-13, BR-20, BR-21.
  - [ ] Semua test lulus.

---

## EPIC 3 — Parent (PAR)

### PAR-01 — Layar Daftar Hadir Parent

- **Priority:** P0
- **Est:** M
- **Depends:** DATA-03, DATA-05
- **Files:** `ui/screen/attendance/ParentAttendanceScreen.kt`
- **Scope:**
  - Header tanggal posyandu.
  - Daftar anak dengan checkbox.
  - Badge validasi.
  - Tombol daftar.
  - Status per anak.
- **Acceptance:**
  - [ ] Hanya anak sendiri yang tampil.
  - [ ] Tanggal aktif tampil jelas.
  - [ ] Status Terdaftar/Selesai/Dibatalkan tampil.
  - [ ] Loading/empty/error ditangani.

### PAR-02 — Daftar multi-anak dan pembatalan

- **Priority:** P0
- **Est:** M
- **Depends:** PAR-01
- **Files:** `ParentAttendanceScreen.kt`, `PosyanduViewModel.kt`
- **Scope:**
  - Pilih beberapa anak.
  - Submit sekali untuk semua.
  - Submit ulang tanpa duplikat.
  - Batal sebelum diukur.
- **Acceptance:**
  - [ ] 2 anak menghasilkan 2 entri.
  - [ ] Submit ulang tidak menggandakan.
  - [ ] Batal mengubah status menjadi `canceled`.
  - [ ] Batal setelah diukur ditolak dengan pesan.

### PAR-03 — Aksi Lihat Hasil

- **Priority:** P1
- **Est:** S
- **Depends:** PAR-01
- **Files:** `ParentAttendanceScreen.kt`, `UkhuwahApp.kt`
- **Scope:**
  - Tombol "Lihat Hasil" untuk entri `measured`.
  - Navigasi ke statistik/riwayat dengan anak terpilih.
- **Acceptance:**
  - [ ] Tombol hanya muncul untuk status `measured`.
  - [ ] Layar tujuan menampilkan anak yang benar.
  - [ ] Back kembali ke daftar hadir.

### PAR-04 — Empty state dan tanggal terakhir

- **Priority:** P1
- **Est:** S
- **Depends:** PAR-01
- **Files:** `ParentAttendanceScreen.kt`
- **Scope:**
  - Empty state saat tidak ada tanggal aktif.
  - Tampilkan tanggal terakhir yang diikuti secara read-only.
  - Pesan "Belum ada tanggal posyandu yang dibuka kader."
- **Acceptance:**
  - [ ] Tombol daftar tidak aktif saat tidak ada tanggal.
  - [ ] Tanggal terakhir hanya menampilkan data sendiri.
  - [ ] Tidak ada akses daftar saat read-only.

### PAR-05 — Banner tanggal aktif di Home Parent

- **Priority:** P1
- **Est:** S
- **Depends:** PAR-01
- **Files:** `ui/screen/HomeParentScreen.kt`
- **Scope:**
  - Kartu/banner jika ada tanggal aktif.
  - Klik membuka Daftar Hadir.
  - Jika tidak ada, tampilkan status netral.
- **Acceptance:**
  - [ ] Banner muncul hanya saat ada tanggal `open`.
  - [ ] Klik membuka layar yang benar.
  - [ ] Layout tidak menabrak menu yang sudah ada.

### PAR-06 — Alur tambah anak untuk Parent

- **Priority:** P0
- **Est:** M
- **Depends:** DATA-06
- **Files:** `ui/screen/family/AddChildrenScreen.kt`
- **Scope:**
  - Parent tidak perlu mencari dirinya sendiri.
  - Gunakan `users_id` dari sesi login.
  - Anak baru `pending`.
  - Pisahkan atau relabel data lahir agar tidak dianggap pengukuran.
  - Validasi field wajib.
- **Acceptance:**
  - [ ] Parent bisa menambah anak tanpa memilih orang tua.
  - [ ] Anak tersimpan dengan `users_id` yang benar.
  - [ ] `validation_status = pending`.
  - [ ] Data lahir tidak membuat baris `measurements`.

### PAR-07 — Kunci identitas anak terverifikasi

- **Priority:** P1
- **Est:** M
- **Depends:** PAR-06
- **Files:** `ui/screen/family/ProfileChildrenScreen.kt`
- **Scope:**
  - Jika `verified`, field identitas disabled untuk Parent.
  - Tampilkan badge "Terverifikasi".
  - Jika `pending`/`rejected`, Parent bisa edit dan submit ulang.
  - Kader tetap bisa edit.
- **Acceptance:**
  - [ ] Parent tidak bisa mengubah nama/NIK/tanggal lahir/jenis kelamin anak verified.
  - [ ] Pesan jelas bahwa perubahan harus lewat kader.
  - [ ] Anak rejected bisa diperbaiki.

### PAR-08 — Isolasi data di Statistik dan Riwayat

- **Priority:** P0
- **Est:** M
- **Depends:** DATA-06
- **Files:** `ui/screen/detail/DetailScreen.kt`, `ui/screen/HistoryScreen.kt`
- **Scope:**
  - Ganti sumber daftar anak menjadi scoped Parent.
  - Pastikan dropdown anak tidak menampilkan anak orang lain.
  - Pastikan pengukuran yang tampil hanya milik anak sendiri.
- **Acceptance:**
  - [ ] Parent A tidak melihat nama/anak Parent B.
  - [ ] Grafik dan riwayat sesuai anak yang dipilih.
  - [ ] Kader tetap melihat semua.

### PAR-09 — Navigasi, menu, dan bottom bar Parent

- **Priority:** P0
- **Est:** S
- **Depends:** PAR-01
- **Files:** `UkhuwahApp.kt`, `data/model/MenuItem.kt`, `ui/components/BottomNavItem.kt`, `BottomBarParent.kt`
- **Scope:**
  - Route baru `attendance_parent`.
  - Menu "Daftar Hadir Posyandu".
  - Bottom bar tetap 4 item atau sesuaikan.
  - Pastikan role Parent tidak bisa membuka route Kader.
- **Acceptance:**
  - [ ] Menu membuka layar daftar hadir.
  - [ ] Bottom bar tidak pecah.
  - [ ] Route Kader tidak bisa diakses Parent.

### PAR-10 — UI test alur Parent

- **Priority:** P1
- **Est:** M
- **Depends:** PAR-01..PAR-09
- **Files:** `app/src/androidTest/...`
- **Scope:**
  - Empty state.
  - Daftar 2 anak.
  - Submit ulang.
  - Batal.
  - Lihat hasil.
- **Acceptance:**
  - [ ] Test berjalan di emulator.
  - [ ] Tidak flaky.

---

## EPIC 4 — Kader (KDR)

### KDR-01 — Layar Daftar Hadir Kader

- **Priority:** P0
- **Est:** M
- **Depends:** DATA-03, DATA-05
- **Files:** `ui/screen/attendance/KaderAttendanceScreen.kt`
- **Scope:**
  - Header tanggal dan status.
  - Ringkasan jumlah.
  - Daftar grouped by Parent.
  - Tombol buka/tutup/ubah tanggal.
- **Acceptance:**
  - [ ] Tidak ada tanggal → CTA buka pendaftaran.
  - [ ] Ada tanggal → daftar dan ringkasan tampil.
  - [ ] Tombol aksi sesuai status.
  - [ ] Loading/empty/error ditangani.

### KDR-02 — Buka tanggal posyandu

- **Priority:** P0
- **Est:** M
- **Depends:** KDR-01, DB-07
- **Files:** `KaderAttendanceScreen.kt`, `OpenPosyanduDateDialog.kt`
- **Scope:**
  - Date picker default hari ini WITA.
  - Tolak tanggal lampau.
  - Tolak jika sudah ada tanggal open.
  - Konfirmasi.
- **Acceptance:**
  - [ ] Tanggal hari ini berhasil dibuka.
  - [ ] Tanggal mendatang berhasil dibuka.
  - [ ] Tanggal lampau ditolak.
  - [ ] Tanggal kedua saat masih ada tanggal aktif ditolak.
  - [ ] Tanggal duplikat ditolak.

### KDR-03 — Tutup, buka kembali, dan ubah tanggal

- **Priority:** P0
- **Est:** M
- **Depends:** KDR-02, DB-07
- **Files:** `KaderAttendanceScreen.kt`, dialog terkait
- **Scope:**
  - Tutup dengan peringatan jumlah belum diukur.
  - Buka kembali.
  - Ubah tanggal hanya jika belum ada entri.
- **Acceptance:**
  - [ ] Tutup menampilkan konfirmasi.
  - [ ] Setelah tutup, daftar read-only.
  - [ ] Buka kembali mengembalikan status open.
  - [ ] Ubah tanggal setelah ada entri ditolak.

### KDR-04 — Ringkasan, grouping, search, dan filter

- **Priority:** P1
- **Est:** M
- **Depends:** KDR-01
- **Files:** `KaderAttendanceScreen.kt`, komponen pendukung
- **Scope:**
  - Hitung terdaftar/belum diukur/sudah diukur/perlu validasi.
  - Group by Parent.
  - Search nama Parent/anak.
  - Filter status.
- **Acceptance:**
  - [ ] Angka ringkasan sesuai daftar.
  - [ ] Search dan filter bekerja.
  - [ ] Grouping tidak menghilangkan anak.
  - [ ] Performa tetap baik untuk 100+ anak.

### KDR-05 — Dialog validasi anak

- **Priority:** P0
- **Est:** M
- **Depends:** DB-08
- **Files:** `ui/screen/attendance/ValidateChildDialog.kt`
- **Scope:**
  - Tampilkan identitas.
  - Kader bisa koreksi nama/NIK/tanggal lahir/jenis kelamin.
  - Verifikasi atau tolak dengan catatan.
  - Setelah verifikasi, lanjut ukur.
- **Acceptance:**
  - [ ] Anak pending tidak bisa langsung ukur.
  - [ ] Verifikasi menyimpan perubahan dan status.
  - [ ] Tolak menyimpan alasan.
  - [ ] Anak rejected bisa diperbaiki.

### KDR-06 — Form pengukuran dari daftar hadir

- **Priority:** P0
- **Est:** L
- **Depends:** KDR-05, DATA-04
- **Files:** `ui/screen/attendance/MeasurementFormScreen.kt`
- **Scope:**
  - Dibuka dari entri daftar hadir.
  - Tidak ada pencarian anak dan tidak ada input tanggal.
  - BB/TB wajib; LK/LILA opsional.
  - Validasi range.
  - Tampilkan identitas dan usia.
  - Metode input: manual; device disembunyikan/di-disable untuk MVP.
- **Acceptance:**
  - [ ] Form hanya bisa dibuka dari entri.
  - [ ] BB/TB kosong ditolak.
  - [ ] LK/LILA kosong diterima.
  - [ ] Range tidak wajar ditolak.
  - [ ] Tidak ada field tanggal.
  - [ ] Tidak ada akses dari profil anak.

### KDR-07 — Simpan dan koreksi pengukuran

- **Priority:** P0
- **Est:** M
- **Depends:** KDR-06, DB-08
- **Files:** `MeasurementFormScreen.kt`, `MeasurementViewModel.kt`
- **Scope:**
  - Simpan lewat RPC.
  - Set status entri measured.
  - Refresh daftar.
  - Jika sudah ada pengukuran tanggal sama, masuk mode koreksi.
- **Acceptance:**
  - [ ] Pengukuran tersimpan dan tampil di statistik/riwayat.
  - [ ] Entri berubah Selesai.
  - [ ] Koreksi tidak membuat baris baru.
  - [ ] Error ditampilkan dengan jelas.

### KDR-08 — Walk-in

- **Priority:** P0
- **Est:** M
- **Depends:** KDR-01, KDR-06, DB-08
- **Files:** `ui/screen/attendance/WalkInScreen.kt`
- **Scope:**
  - Cari Parent/anak.
  - Tambah entri `source = kader`.
  - Lanjut ke validasi/ukur.
  - Tolak duplikat.
- **Acceptance:**
  - [ ] Walk-in hanya saat tanggal open dan tanggal <= hari ini.
  - [ ] Entri daftar hadir dibuat sebelum pengukuran.
  - [ ] Anak yang sudah terdaftar tidak bisa di-walk-in dua kali.
  - [ ] Anak baru bisa ditambahkan kader jika belum ada.

### KDR-09 — Home Kader dan navigasi

- **Priority:** P1
- **Est:** S
- **Depends:** KDR-01
- **Files:** `ui/screen/HomeScreen.kt`, `UkhuwahApp.kt`, `BottomNavItem.kt`, `BottomBar.kt`, `MenuItem.kt`
- **Scope:**
  - Kartu status tanggal posyandu.
  - Menu "Daftar Hadir Posyandu".
  - Bottom bar central action diarahkan ke daftar hadir, bukan langsung ke ukur.
- **Acceptance:**
  - [ ] Home menampilkan tanggal dan ringkasan.
  - [ ] Menu membuka layar yang benar.
  - [ ] Tidak ada tombol ukur langsung dari home.
  - [ ] Route lama `update-children` tidak dipakai untuk ukur mandiri.

### KDR-10 — Nonaktifkan device untuk MVP

- **Priority:** P1
- **Est:** S
- **Depends:** KDR-06
- **Files:** `MeasurementFormScreen.kt`
- **Scope:**
  - Sembunyikan atau disable opsi "Alat".
  - Jika ditampilkan, beri label "Fase 2".
- **Acceptance:**
  - [ ] Tidak ada alur device yang bisa diselesaikan di MVP.
  - [ ] Tidak ada tombol "Mulai Pengukuran" yang tidak berfungsi.

### KDR-11 — UI test alur Kader

- **Priority:** P1
- **Est:** M
- **Depends:** KDR-01..KDR-09
- **Files:** `app/src/androidTest/...`
- **Scope:**
  - Buka/tutup tanggal.
  - Validasi.
  - Ukur.
  - Walk-in.
  - Koreksi.
- **Acceptance:**
  - [ ] Test berjalan di emulator.
  - [ ] Tidak flaky.

---

## EPIC 5 — Import Data Lama (IMP)

### IMP-01 — Rewrite script import

- **Priority:** P0
- **Est:** L
- **Depends:** DB-08
- **Files:** `import_database/import.js`
- **Scope:**
  - Import auth users, `users`, `children`, `measurements`.
  - Gunakan service role hanya di script lokal.
  - Tidak ada password default atau password di log.
  - Anak hasil import `verified`.
  - Pengukuran `source = 'migration'`, `measure_date` WITA.
  - Batch dan error collection.
- **Acceptance:**
  - [ ] Import berjalan di staging.
  - [ ] Tidak ada password di log.
  - [ ] Anak verified.
  - [ ] Pengukuran punya `measure_date`.
  - [ ] Script berhenti dengan kode error jika ada kegagalan kritis.

### IMP-02 — Mapping CSV dan laporan dry-run

- **Priority:** P0
- **Est:** M
- **Depends:** IMP-01
- **Files:** `import_database/README.md`, `import_database/data/*.csv`
- **Scope:**
  - Dokumentasikan mapping kolom lama → baru.
  - Mode `DRY_RUN=true`.
  - Laporan: dibuat, dilewati, perlu review.
  - Validasi tanggal, gender, NIK, nilai BB/TB/LK/LILA.
- **Acceptance:**
  - [ ] Dry-run tidak menulis data.
  - [ ] Laporan bisa dibaca non-teknis.
  - [ ] Data invalid tidak menggagalkan seluruh batch.

### IMP-03 — Idempotensi dan uji import ulang

- **Priority:** P0
- **Est:** M
- **Depends:** IMP-02
- **Files:** `import_database/import.js`
- **Scope:**
  - Upsert users by email.
  - Upsert children by NIK atau Parent+nama+tanggal lahir.
  - Upsert measurements by child + measure_date.
  - Jalankan dua kali di staging.
- **Acceptance:**
  - [ ] Run kedua tidak menambah data.
  - [ ] Laporan menunjukkan jumlah dilewati.
  - [ ] Tidak ada duplikat.

### IMP-04 — Import staging dan perbaikan anomali

- **Priority:** P0
- **Est:** M
- **Depends:** IMP-03
- **Files:** laporan import
- **Scope:**
  - Import data asli di staging.
  - Review anak tanpa Parent, NIK duplikat, tanggal tidak valid, nilai ekstrem.
  - Perbaiki mapping atau data sumber.
- **Acceptance:**
  - [ ] Semua anak punya Parent.
  - [ ] Tidak ada pengukuran orphan.
  - [ ] Anomali terdokumentasi dan disetujui.
  - [ ] Jumlah data sesuai sumber.

### IMP-05 — Runbook import produksi

- **Priority:** P0
- **Est:** S
- **Depends:** IMP-04
- **Files:** `import_database/README.md`
- **Scope:**
  - Backup.
  - Urutan eksekusi.
  - Verifikasi pasca-import.
  - Rollback.
  - Kontak penanggung jawab.
- **Acceptance:**
  - [ ] Runbook bisa diikuti orang lain.
  - [ ] Query verifikasi tersedia.
  - [ ] Rollback diuji di staging.

---

## EPIC 6 — QA dan Rilis (QA)

### QA-01 — Unit test suite

- **Priority:** P1
- **Est:** M
- **Depends:** DATA-07
- **Files:** `app/src/test/...`
- **Scope:**
  - Tanggal WITA.
  - Status tanggal/validasi/daftar hadir.
  - Validasi measurement.
  - Idempotensi submit.
- **Acceptance:**
  - [ ] `./gradlew test` lulus.
  - [ ] Coverage logika kritis memadai.

### QA-02 — Compose UI test

- **Priority:** P1
- **Est:** L
- **Depends:** PAR-10, KDR-11
- **Files:** `app/src/androidTest/...`
- **Scope:**
  - Parent: empty, daftar 2 anak, batal.
  - Kader: buka/tutup, validasi, ukur, walk-in.
- **Acceptance:**
  - [ ] Test berjalan di emulator API 26+.
  - [ ] Tidak flaky.

### QA-03 — SQL/RLS test suite

- **Priority:** P0
- **Est:** L
- **Depends:** DB-09
- **Files:** `supabase/tests/*.sql`
- **Scope:**
  - Parent A/B isolation.
  - Parent tidak bisa menulis measurement.
  - Parent tidak bisa mengubah validasi.
  - Satu tanggal open.
  - Duplikat daftar hadir/pengukuran.
  - Tanggal closed menolak aksi.
- **Acceptance:**
  - [ ] Semua test lulus.
  - [ ] Tidak ada policy yang terlalu longgar.
  - [ ] Hasil disimpan di laporan QA.

### QA-04 — Manual QA AC-01..AC-27

- **Priority:** P0
- **Est:** M
- **Depends:** PAR, KDR, IMP
- **Files:** `docs/QA-CHECKLIST.md`
- **Scope:**
  - Jalankan seluruh Acceptance Criteria PRD.
  - Uji dua akun Parent.
  - Uji kader.
  - Uji tanggal lampau/mendatang.
  - Uji sebelum/sesudah tutup.
- **Acceptance:**
  - [ ] Semua AC lulus atau punya bug terdaftar.
  - [ ] Bukti screenshot/video untuk AC kritis.
  - [ ] Sign-off dari pemilik produk.

### QA-05 — Regresi web

- **Priority:** P0
- **Est:** S
- **Depends:** IMP-04
- **Files:** — (web eksternal)
- **Scope:**
  - Dashboard membaca pengukuran baru.
  - Ekspor puskesmas.
  - Riwayat anak.
  - Role Parent/Admin tidak berubah.
- **Acceptance:**
  - [ ] Tidak ada error kolom.
  - [ ] Data pengukuran baru dan hasil import tampil.
  - [ ] Ekspor menghasilkan file yang benar.

### QA-06 — Performa dan error handling

- **Priority:** P1
- **Est:** S
- **Depends:** KDR-04
- **Files:** — 
- **Scope:**
  - Daftar 100+ anak.
  - Koneksi lambat/offline.
  - Submit ganda.
  - Refresh berulang.
- **Acceptance:**
  - [ ] Daftar tetap responsif.
  - [ ] Offline menampilkan pesan, bukan crash.
  - [ ] Submit ganda tidak membuat duplikat.

### QA-07 — Release build dan distribusi

- **Priority:** P0
- **Est:** S
- **Depends:** QA-04..QA-06
- **Files:** `app/build.gradle.kts`
- **Scope:**
  - Naikkan `versionCode` dan `versionName`.
  - Build release.
  - Signing.
  - Distribusi internal/Play.
  - Catatan rilis.
- **Acceptance:**
  - [ ] Release build terpasang di perangkat uji.
  - [ ] Login, daftar hadir, ukur berjalan.
  - [ ] Tidak ada `BuildConfig.DEBUG` logging sensitif.

### QA-08 — Runbook kader dan training

- **Priority:** P1
- **Est:** S
- **Depends:** QA-04
- **Files:** `docs/RUNBOOK-KADER.md`
- **Scope:**
  - Buka/tutup tanggal.
  - Validasi.
  - Ukur.
  - Walk-in.
  - Koreksi.
  - Apa yang dilakukan saat offline.
- **Acceptance:**
  - [ ] Panduan satu halaman.
  - [ ] Kader bisa mengikuti tanpa pendamping.
  - [ ] Ada kontak bantuan.

---

## 5. File Impact Map

| Area | File utama |
|---|---|
| Database | `supabase/schema.sql`, `supabase/migrations/*.sql`, `supabase/seed.sql`, `supabase/tests/*.sql` |
| Model/DTO | `data/model/Posyandu.kt`, `data/dto/PosyanduDto.kt` |
| Service | `data/supabase/PosyanduService.kt`, `AttendanceService.kt`, `MeasurementService.kt` |
| ViewModel | `ui/viewmodel/PosyanduViewModel.kt`, `MeasurementViewModel.kt` |
| Parent UI | `ui/screen/attendance/ParentAttendanceScreen.kt` |
| Kader UI | `ui/screen/attendance/KaderAttendanceScreen.kt`, `MeasurementFormScreen.kt`, `WalkInScreen.kt`, `ValidateChildDialog.kt` |
| Navigasi | `UkhuwahApp.kt`, `data/model/MenuItem.kt`, `ui/components/BottomNavItem.kt`, `BottomBar.kt`, `BottomBarParent.kt` |
| Existing fix | `AppTopBar.kt`, `ChildService.kt`, `ChildViewModel.kt`, `AuthViewModel.kt`, `DetailScreen.kt`, `HistoryScreen.kt`, `ProfileChildrenScreen.kt`, `AddChildrenScreen.kt`, `NewMeasureChildrenScreen.kt` |
| Import | `import_database/import.js`, `import_database/README.md`, `import_database/data/*.csv` |
| Docs | `docs/SCHEMA.md`, `docs/WEB-CONTRACT.md`, `docs/QA-CHECKLIST.md`, `docs/RUNBOOK-KADER.md` |

---

## 6. Traceability — PRD ke Task

| Acceptance Criteria | Task utama |
|---|---|
| AC-01..AC-05 tanggal posyandu | DB-01, DB-07, KDR-02, KDR-03 |
| AC-06..AC-09 daftar hadir & validasi | PAR-02, KDR-05, DB-08 |
| AC-10..AC-12 pengukuran | KDR-06, KDR-07, DB-08 |
| AC-13..AC-15 pembatalan | PAR-02, DB-07 |
| AC-16 walk-in | KDR-08, DB-08 |
| AC-17..AC-19 tutup/buka/ubah tanggal | KDR-03, DB-07 |
| AC-20 isolasi Parent | DB-06, DATA-06, PAR-08 |
| AC-21 import | IMP-01..IMP-05 |
| AC-22 web | DB-10, QA-05 |
| AC-23 WITA | DB-05, DATA-05 |
| AC-24 kunci identitas | PAR-07 |
| AC-25 tanggal baru setelah tutup | KDR-02, KDR-03 |
| AC-26..AC-27 ukur hanya dari daftar hadir | KDR-06, KDR-08, DB-08 |

---

## 7. Test Plan Ringkas

### 7.1 Unit test

- [ ] `todayWita()` benar pada 00:00–01:00 WITA.
- [ ] Status `open` hanya bisa satu.
- [ ] Tanggal lampau tidak bisa dibuka.
- [ ] Parent tidak bisa daftar saat `closed`.
- [ ] Anak `pending` tidak bisa diukur.
- [ ] BB/TB wajib; LK/LILA opsional.
- [ ] Koreksi tidak membuat baris baru.
- [ ] Submit ganda tidak membuat duplikat.

### 7.2 SQL/RLS test

- [ ] Parent A tidak melihat anak Parent B.
- [ ] Parent A tidak melihat daftar hadir Parent B.
- [ ] Parent tidak bisa insert/update `measurements`.
- [ ] Parent tidak bisa mengubah `validation_status`.
- [ ] Parent tidak bisa membuka/menutup tanggal.
- [ ] Kader bisa mengelola tanggal dan pengukuran.
- [ ] Delete/update tanggal tidak meninggalkan entri yatim.

### 7.3 Manual QA

- [ ] Android API 26, 30, 34+.
- [ ] Layar kecil dan normal.
- [ ] Koneksi lambat dan offline.
- [ ] Dua akun Parent di satu perangkat bergantian.
- [ ] Tanggal hari ini, mendatang, lampau.
- [ ] Tanggal open/closed.
- [ ] Import ulang.
- [ ] Web dashboard/ekspor.

---

## 8. Release Checklist

- [ ] Semua migrasi diterapkan di staging.
- [ ] Backup produksi dibuat.
- [ ] RLS test lulus.
- [ ] Import staging selesai dan disetujui.
- [ ] AC-01..AC-27 lulus.
- [ ] Web dashboard/ekspor lulus.
- [ ] Tidak ada password/token di log.
- [ ] Akun test dihapus/dinonaktifkan.
- [ ] `versionCode` dan `versionName` dinaikkan.
- [ ] Release build ditandatangani.
- [ ] Runbook kader dibagikan.
- [ ] Kontak bantuan disiapkan.

---

## 9. Risiko dan Blocker

| Risiko | Dampak | Mitigasi | Task |
|---|---|---|---|
| Skema Supabase tidak terdokumentasi | Migrasi gagal | Audit dan baseline dulu | FND-01 |
| RLS terlalu longgar | Data bocor | SQL test dua Parent | DB-06, QA-03 |
| `AppTopBar` rusak | Tidak bisa tambah anak/ukur | Perbaiki sebelum UI | FND-03 |
| Data import kotor | Grafik/laporan salah | Dry-run dan laporan | IMP-02, IMP-04 |
| Duplikat pengukuran | Grafik ganda | Unique constraint + koreksi | DB-04, DB-08 |
| Tanggal WITA salah | Data masuk tanggal salah | Set timezone + test | DB-05, DATA-05 |
| Kader lupa tutup tanggal | Tanggal berikutnya terblokir | Banner + konfirmasi | KDR-03 |
| Web rusak karena kolom baru | Dashboard/ekspor gagal | Kontrak web + regresi | DB-10, QA-05 |
| Tidak ada test infrastructure | Regresi tidak terdeteksi | Scaffolding + test | FND-05, QA-01..03 |
| Pekerjaan bercampur file pribadi | Commit kotor | Branch dan commit disiplin | FND-00 |

---

## 10. Backlog Fase 2

Jangan dikerjakan di MVP:

- [ ] Pengukuran dengan alat IoT.
- [ ] Offline queue dan sinkronisasi.
- [ ] Status `no_show`.
- [ ] Notifikasi hasil.
- [ ] Riwayat daftar hadir beberapa tanggal.
- [ ] Audit log perubahan identitas/pengukuran.
- [ ] Baseline antropometri awal.
- [ ] Jadwal berulang otomatis.
- [ ] Multi-posyandu.
- [ ] Ekspor daftar hadir ke web.
- [ ] Statistik kehadiran per tanggal.

---

## 11. Urutan Mulai yang Disarankan

1. **FND-00 → FND-01 → FND-02** — bersihkan repo, audit skema, siapkan staging.
2. **DB-01 → DB-08** — bangun dan uji database sampai RLS/RPC lulus.
3. **DATA-01 → DATA-05** — model, service, ViewModel, util tanggal.
4. **PAR-01 → PAR-04** dan **KDR-01 → KDR-03** paralel.
5. **KDR-05 → KDR-08** — validasi, ukur, walk-in.
6. **PAR-05 → PAR-09**, **KDR-09 → KDR-10** — home, menu, navigasi.
7. **IMP-01 → IMP-05** — import data.
8. **QA-01 → QA-08** — uji, regresi, rilis.

---

**Akhir dokumen — task breakdown v1.0**
