# PRD — Daftar Hadir Posyandu & Pengukuran

**Produk:** Aplikasi Android Posyandu Bontoulu
**Versi dokumen:** 1.0 (FINAL)
**Tanggal:** 25 November 2026
**Status:** Final — disetujui untuk implementasi
**Cakupan:** Aplikasi Android (Parent & Admin/Kader). Web hanya sebagai konteks integrasi.
**Bahasa:** Indonesia

---

## Daftar Isi

1. Ringkasan Eksekutif
2. Latar Belakang & Masalah
3. Tujuan & Non-Goals
4. Ruang Lingkup
5. Definisi & Glosarium
6. Peran & Hak Akses
7. Alur Utama
8. State Machine
9. Aturan Bisnis
10. Model Data
11. Import Data Lama
12. Otorisasi & RLS
13. Kontrak Integrasi Web
14. Kebutuhan Fungsional
15. Kebutuhan Non-Fungsional
16. Fase Implementasi
17. Acceptance Criteria
18. Prasyarat Teknis & Bug yang Harus Diperbaiki
19. Risiko
20. Pertanyaan Terbuka & Keputusan yang Belum Dikunci
21. Metrik Keberhasilan
22. Lampiran — Teks UI yang Disarankan
23. Ringkasan Keputusan yang Sudah Dikunci

---

## Riwayat Revisi

| Versi | Tanggal | Ringkasan |
|---|---|---|
| 0.1 | 23 November 2026 | Draft awal: daftar hadir harian, validasi anak, pengukuran, walk-in. |
| 0.2 | 23 November 2026 | Sesi posyandu, import data lama, pemisahan status per anak. |
| 0.3 | 25 November 2026 | Sesi dihapus; diganti tanggal posyandu. WITA. Ukur hanya dari daftar hadir. |
| 0.3.1 | 25 November 2026 | Dikunci: hanya satu tanggal posyandu aktif. |
| 1.0 | 25 November 2026 | Final untuk implementasi. |

---

## 1. Ringkasan Eksekutif

Kader membuka **tanggal posyandu** dan mengaktifkan pendaftaran untuk tanggal tersebut. Selama pendaftaran terbuka, orang tua dapat mendaftarkan satu atau lebih anak ke **daftar hadir posyandu** pada tanggal itu.

Kader melihat daftar hadir, memvalidasi data anak, lalu menekan **"Ukur"** untuk mencatat berat badan, tinggi badan, lingkar kepala, dan lingkar lengan anak. Jika ada anak yang datang tetapi belum terdaftar, kader menambahkannya lewat **walk-in**, lalu mengukur.

Tidak ada nomor antrian, tidak ada sesi bernomor, dan tidak ada penjadwalan otomatis. Yang ada hanya **satu tanggal posyandu aktif** yang dibuka kader.

---

## 2. Latar Belakang & Masalah

### 2.1 Kondisi saat ini

- Orang tua dapat menambah data anak, tetapi data langsung dianggap sah tanpa verifikasi kader.
- Kader melihat seluruh data anak tanpa pembatasan yang jelas.
- Belum ada mekanisme "anak siapa yang akan datang pada tanggal posyandu berikutnya".
- Pengukuran bisa dilakukan berulang tanpa aturan yang tegas.
- Data awal anak (TB/BB/LK) disimpan di `children`, sedangkan grafik membaca `measurements`, sehingga data bisa tidak konsisten.
- Belum ada pembatasan akses data antar-orang tua (risiko privasi).
- Aplikasi lama sudah memiliki data yang harus diimpor ke proyek ini.

### 2.2 Masalah utama yang diselesaikan

| Masalah | Dampak | Solusi dalam PRD |
|---|---|---|
| Kader tidak tahu siapa yang akan datang | Persiapan alat dan pencatatan tidak efisien | Tanggal posyandu + daftar hadir |
| Tidak ada kendali kapan pendaftaran dibuka | Orang tua mendaftar untuk tanggal yang tidak jelas | Kader membuka/menutup tanggal posyandu |
| Data anak belum diverifikasi | Data identitas salah memengaruhi z-score dan laporan | Status validasi + validasi kader |
| Pengukuran ganda | Grafik dan laporan ganda | Satu pengukuran per anak per tanggal |
| Orang tua melihat data anak lain | Pelanggaran privasi | RLS + filter query |
| Anak datang tanpa terdaftar | Data tidak tercatat | Walk-in oleh kader |
| Data lama belum masuk | Riwayat anak hilang | Prosedur import data |

---

## 3. Tujuan & Non-Goals

### 3.1 Tujuan

1. Kader dapat menetapkan tanggal posyandu dan membuka pendaftaran untuk tanggal tersebut.
2. Orang tua hanya dapat mendaftarkan anak ketika pendaftaran tanggal posyandu sedang dibuka.
3. Orang tua dapat mendaftarkan satu atau lebih anak pada tanggal tersebut.
4. Kader dapat melihat daftar hadir dan mengukur anak langsung dari daftar.
5. Kader dapat menambahkan anak yang belum terdaftar lewat walk-in.
6. Kader dapat memvalidasi data anak sebelum atau pada saat pengukuran.
7. Setiap pengukuran tercatat sekali per anak per tanggal dan langsung terlihat di statistik/riwayat orang tua.
8. Data antar-orang tua terisolasi dengan benar.
9. Data lama dapat diimpor tanpa kehilangan riwayat dan tanpa membuat duplikat.

### 3.2 Non-Goals (tidak termasuk MVP)

- Nomor antrian, estimasi waktu tunggu, atau slot layanan.
- Sesi bernomor atau beberapa sesi dalam satu tanggal.
- Jadwal posyandu berulang otomatis (recurring schedule).
- Lebih dari satu tanggal posyandu aktif dalam satu waktu.
- Pengukuran mandiri oleh orang tua di rumah.
- Aplikasi web baru atau perubahan desain web.
- Fitur Super Admin di Android.
- Multi-posyandu dalam satu instalasi.
- Mode offline penuh dan sinkronisasi konflik.
- Push notification.
- Integrasi alat IoT (fase 2).
- Import riwayat kehadiran lama (fokus import: akun, anak, pengukuran).
- Pembayaran, resep, rujukan, dan administrasi puskesmas.

---

## 4. Ruang Lingkup

### 4.1 Platform dan peran

| Platform | Peran | Keterangan |
|---|---|---|
| Web | Super Admin | Pegawai puskesmas. Dashboard, riwayat, ekspor. Tidak menggunakan Android. |
| Web | Admin | Kader. Dapat menggunakan web dan Android. |
| Android | Admin | Kader. Membuka tanggal, memvalidasi, melihat daftar hadir, mengukur. |
| Android | Parent | Orang tua. Menginput data anak, mendaftar hadir, melihat hasil. |

**Keputusan role:**
- Nilai role di database **tetap** `Parent` dan `Admin` agar kompatibel dengan web dan kode Android saat ini.
- `Super Admin` hanya ada di web dan tidak masuk alur Android.
- Label UI untuk `Admin` adalah **"Kader"**.

### 4.2 Posyandu

Aplikasi hanya melayani **Posyandu Bontoulu**. Tidak ada `posyandu_id` pada skema MVP. Jika suatu saat dibutuhkan multi-posyandu, penambahan kolom dilakukan lewat migrasi terpisah.

---

## 5. Definisi & Glosarium

| Istilah | Definisi |
|---|---|
| **Tanggal Posyandu** | Tanggal pelaksanaan posyandu yang dibuka kader. Satu tanggal = satu baris data. Tidak ada penomoran sesi. |
| **Pendaftaran Terbuka** | Status tanggal posyandu yang memungkinkan orang tua mendaftarkan anak. |
| **Pendaftaran Ditutup** | Status tanggal posyandu yang tidak menerima pendaftaran baru. Hanya baca. |
| **Daftar Hadir Posyandu** | Daftar anak yang didaftarkan untuk satu tanggal posyandu. Bukan nomor antrian. |
| **Entri Daftar Hadir** | Satu baris yang menghubungkan satu anak dengan satu tanggal posyandu. |
| **Ukur** | Kegiatan kader mencatat berat badan (BB), tinggi badan (TB), lingkar kepala (LK), dan lingkar lengan (LILA) anak. Tombol "Ukur" membuka form pengukuran. |
| **Walk-in** | Anak yang datang tanpa terdaftar. Kader menambahkannya ke daftar hadir tanggal tersebut, lalu mengukur. |
| **Parent** | Orang tua/wali. Pemilik akun dan data anak. |
| **Admin / Kader** | Petugas posyandu. Satu-satunya peran yang membuka tanggal dan mengukur. |
| **Validasi** | Pemeriksaan identitas anak (nama, NIK, tanggal lahir, jenis kelamin) oleh kader. |

**Aturan istilah:** jangan gunakan kata "antrian" atau "sesi" di UI. Gunakan **"Daftar Hadir Posyandu"** dan **"Tanggal Posyandu"**.

---

## 6. Peran & Hak Akses

### 6.1 Matriks hak akses aplikasi

| Kemampuan | Parent | Admin/Kader | Super Admin (web) |
|---|---|---|---|
| Registrasi akun orang tua | Ya | Tidak | — |
| Tambah data anak | Ya | Ya (walk-in) | — |
| Edit identitas anak | Hanya saat `pending`/`rejected` | Ya | — |
| Validasi data anak | Tidak | Ya | — |
| Buka/tutup tanggal posyandu | Tidak | Ya | — |
| Lihat tanggal aktif | Ya | Ya | — |
| Lihat tanggal lampau | Hanya yang diikuti | Semua | — |
| Daftar hadir | Anak sendiri | Semua anak | — |
| Batalkan pendaftaran | Sebelum diukur & pendaftaran masih terbuka | Sebelum diukur | — |
| Ukur anak | Tidak | Ya | — |
| Lihat hasil pengukuran | Anak sendiri | Semua anak | Semua |
| Kelola akun kader | Tidak | Tidak | Ya (web) |

### 6.2 Aturan otorisasi

- Semua pembatasan **wajib** ditegakkan di database melalui Row Level Security (RLS), bukan hanya di UI.
- Query Android juga harus memfilter berdasarkan `auth.uid()` untuk orang tua.
- Kader tidak boleh mengubah role pengguna lain dari aplikasi Android.
- Operasi tulis yang melibatkan beberapa tabel (daftar hadir + pengukuran) dilakukan melalui RPC transaksional.

---

## 7. Alur Utama

### 7.1 Kader menetapkan tanggal posyandu

```
Login sebagai Admin/Kader
  → menu "Daftar Hadir Posyandu"
  → jika belum ada tanggal aktif, tampil tombol "Buka Pendaftaran Posyandu"
  → kader memilih tanggal (default hari ini; boleh tanggal mendatang)
  → konfirmasi
  → tanggal posyandu dibuat dengan status pendaftaran terbuka
  → orang tua sekarang dapat melihat dan mendaftar
```

Ketentuan:
- Hanya satu tanggal posyandu yang boleh berstatus terbuka pada satu waktu.
- Hanya boleh ada satu baris per tanggal.
- Tanggal tidak boleh di masa lalu saat dibuat.
- Tanggal masih dapat diubah selama **belum ada entri daftar hadir**. Setelah ada entri, tanggal terkunci.
- Tanggal mendatang boleh dibuka, tetapi pengukuran belum bisa dilakukan sampai tanggal tersebut tiba.
- Disarankan kader membuka tanggal pada hari pelaksanaan atau H-1, agar tanggal aktif selalu relevan dengan walk-in hari ini.

### 7.2 Parent mendaftarkan anak

```
Buka aplikasi
  → menu "Daftar Hadir Posyandu"
  → jika ada tanggal posyandu terbuka:
       tampil tanggal + daftar anak miliknya
       orang tua mencentang satu atau lebih anak
       tekan "Daftarkan"
       status per anak: "Terdaftar"
  → jika tidak ada tanggal terbuka:
       tampil "Belum ada tanggal posyandu yang dibuka kader."
       tombol daftar tidak aktif
```

Ketentuan:
- Pendaftaran hanya boleh jika tanggal posyandu **terbuka** dan **tanggal >= hari ini**.
- Satu kali submit boleh berisi banyak anak.
- Submit ulang untuk anak yang sama tidak membuat duplikat.
- Jika anak sudah diukur pada tanggal itu, checkbox dikunci dan statusnya "Selesai".
- Jika anak dibatalkan, status "Dibatalkan" dan dapat didaftarkan kembali selama pendaftaran masih terbuka.
- Orang tua tidak dapat memilih tanggal; tanggal ditentukan kader.

### 7.3 Kader membuka daftar hadir

```
Login sebagai Admin/Kader
  → menu "Daftar Hadir Posyandu"
  → melihat tanggal aktif + status
  → melihat ringkasan: Terdaftar / Belum diukur / Sudah diukur / Perlu validasi
  → melihat daftar dikelompokkan per orang tua
  → memilih anak → "Ukur" atau "Validasi & Ukur"
```

Contoh tampilan:

```
Posyandu: Sabtu, 28 November 2026          [Pendaftaran Terbuka]
Terdaftar 12 · Belum diukur 5 · Sudah diukur 7 · Perlu validasi 2

Ibu Ani
  - Budi (3 tahun)    [Terdaftar]        [Ukur]
  - Sari (1 tahun)    [Selesai]          [Lihat Hasil]

Ibu Rina
  - Andi (2 tahun)    [Perlu Validasi]   [Validasi & Ukur]
```

### 7.4 Validasi data anak (inline)

Validasi dilakukan **saat kader menekan "Ukur"**, bukan sebagai layar terpisah di MVP.

```
Kader tekan "Ukur"
  → tampil ringkasan identitas anak
  → kader periksa dan boleh mengoreksi nama/NIK/tanggal lahir/jenis kelamin
  → tekan "Validasi & Lanjut"
  → validation_status = verified
  → lanjut ke form pengukuran
```

Ketentuan:
- Anak `pending` tetap boleh masuk daftar hadir.
- Anak `pending` **tidak boleh** langsung diukur tanpa validasi.
- Anak `rejected` harus diperbaiki orang tua atau kader sebelum diukur.
- Setelah `verified`, orang tua tidak dapat mengubah identitas anak.

### 7.5 Apa itu "Ukur" dan bagaimana alurnya

**"Ukur" bukan fitur terpisah.** Ini adalah aksi kader untuk mencatat hasil pengukuran anak dari daftar hadir.

Yang dicatat:

| Field | Wajib | Keterangan |
|---|---|---|
| Berat badan (BB) | Ya | Dalam kg |
| Tinggi badan (TB) | Ya | Dalam cm |
| Lingkar kepala (LK) | Tidak | Dalam cm |
| Lingkar lengan (LILA) | Tidak | Dalam cm |

Untuk apa hasilnya:
- Menghitung status gizi/z-score anak.
- Mengisi grafik Statistik Pertumbuhan dan Perjalanan Pertumbuhan.
- Mengisi riwayat pengukuran orang tua.
- Menjadi data dashboard dan ekspor puskesmas di web.

Alur:

```
Pilih anak dari daftar hadir
  → tekan "Ukur"
  → jika data anak belum valid: validasi dulu
  → isi BB, TB, LK, LILA
  → tekan "Simpan"
  → sistem menyimpan pengukuran dan menautkannya ke entri daftar hadir
  → status entri menjadi "Selesai"
  → hasil langsung muncul di statistik dan riwayat orang tua
```

Ketentuan:
- Pengukuran hanya boleh jika tanggal posyandu **terbuka** dan **tanggal <= hari ini**.
- **Pengukuran hanya bisa dimulai dari entri daftar hadir.** Tidak ada tombol ukur langsung dari profil anak.
- Jika anak belum ada di daftar hadir, kader harus menambahkan walk-in terlebih dahulu.
- Tanggal pengukuran diambil dari server, bukan input manual.
- Form tidak menampilkan field tanggal.
- Jika anak sudah diukur pada tanggal yang sama, tombol berubah menjadi "Lihat Hasil" dan "Koreksi" (khusus kader).
- Koreksi memperbarui baris pengukuran yang sama, bukan membuat baris baru.

### 7.6 Walk-in

```
Kader → "Tambah Walk-in"
  → cari nama orang tua / anak
  → pilih anak
  → sistem membuat entri pada tanggal posyandu aktif dengan source = kader
  → lanjut validasi (jika perlu)
  → lanjut "Ukur"
```

Walk-in wajib ada di MVP karena:
- Orang tua tidak punya HP.
- Orang tua lupa mendaftar.
- Koneksi orang tua bermasalah.
- Anak datang tanpa didaftarkan.

Ketentuan:
- Walk-in hanya bisa dilakukan jika ada tanggal posyandu terbuka dan tanggalnya <= hari ini.
- Walk-in **selalu membuat entri daftar hadir terlebih dahulu**, baru pengukuran.
- Anak yang sudah punya entri pada tanggal itu tidak perlu walk-in; gunakan entri yang ada.

### 7.7 Orang tua melihat hasil

```
Setelah kader menyimpan pengukuran
  → entri berubah "Selesai"
  → orang tua membuka Statistik Pertumbuhan / Riwayat
  → data baru tampil
```

MVP tidak menggunakan push notification. Indikator cukup berupa status di halaman daftar hadir dan data yang sudah diperbarui saat halaman dibuka.

### 7.8 Pembatalan

- Orang tua dapat membatalkan anak yang belum diukur selama pendaftaran masih terbuka dan tanggal belum lewat.
- Setelah diukur, pembatalan ditolak; tampilkan hasil.
- Setelah pendaftaran ditutup, orang tua tidak dapat membatalkan.
- Kader dapat membatalkan entri yang salah masuk dan belum diukur.

### 7.9 Kader menutup pendaftaran

```
Kader → "Tutup Pendaftaran"
  → sistem menampilkan jumlah anak yang belum diukur
  → kader konfirmasi
  → status tanggal menjadi tertutup
  → tidak ada pendaftaran baru
  → daftar menjadi read-only
```

Ketentuan:
- Tanggal yang ditutup dapat dibuka kembali oleh kader jika masih diperlukan.
- Membuka kembali diperbolehkan selama tidak ada tanggal lain yang sedang terbuka.
- Setelah ditutup, kader tidak dapat mengukur lagi sampai dibuka kembali.

---

## 8. State Machine

### 8.1 Status tanggal posyandu (`posyandu_dates.status`)

```
open ──(kader tutup)──> closed
  ^                       │
  └──(kader buka kembali)─┘
```

| Status | Arti | Parent boleh daftar? | Kader boleh ukur? |
|---|---|---|---|
| `open` | Pendaftaran dibuka | Ya, jika tanggal >= hari ini | Ya, jika tanggal <= hari ini |
| `closed` | Pendaftaran ditutup | Tidak | Tidak |

Aturan tambahan:
- Maksimal satu tanggal `open` dalam satu waktu.
- Satu tanggal hanya boleh memiliki satu baris.
- Tanggal tidak boleh diubah jika sudah ada entri daftar hadir.

**Catatan:** ini bukan sesi bernomor. `posyandu_dates` hanya menyimpan tanggal dan status pendaftarannya.

### 8.2 Status validasi anak (`children.validation_status`)

```
pending ──(kader validasi)──> verified
   │                             │
   │ (kader tolak)               │ (kader buka koreksi)
   ▼                             ▼
rejected ──(orang tua/kader perbaiki)──> pending
```

| Status | Arti | Boleh didaftarkan? | Boleh diukur? |
|---|---|---|---|
| `pending` | Baru diinput, belum diperiksa | Ya | Tidak |
| `verified` | Sudah divalidasi kader | Ya | Ya |
| `rejected` | Ditolak, perlu perbaikan | Ya (dengan label) | Tidak |

### 8.3 Status entri daftar hadir (`attendance_items.status`)

```
registered ──(kader simpan pengukuran)──> measured
     │
     └──(orang tua/kader batalkan)──> canceled
```

| Status | Label UI | Arti |
|---|---|---|
| `registered` | Terdaftar | Sudah didaftarkan, belum diukur |
| `measured` | Selesai | Sudah diukur pada tanggal ini |
| `canceled` | Dibatalkan | Dibatalkan sebelum diukur |

`no_show` tidak termasuk MVP. Jika nanti dibutuhkan, ditambahkan sebagai status baru.

---

## 9. Aturan Bisnis

| ID | Aturan |
|---|---|
| **BR-01** | Tanggal posyandu dan tanggal pengukuran ditentukan server, bukan jam perangkat. |
| **BR-02** | Satu anak hanya boleh memiliki satu entri daftar hadir per tanggal posyandu. Submit ulang bersifat upsert. |
| **BR-03** | Satu anak hanya boleh memiliki satu pengukuran per tanggal. Koreksi memperbarui baris yang sama. |
| **BR-04** | Status disimpan per anak, bukan per orang tua. |
| **BR-05** | Anak `pending` boleh masuk daftar hadir, tetapi wajib divalidasi sebelum diukur. |
| **BR-06** | Setelah `verified`, identitas anak terkunci untuk orang tua; perubahan hanya oleh kader. |
| **BR-07** | Orang tua hanya dapat melihat dan mengubah data miliknya sendiri. |
| **BR-08** | Pengukuran hanya dapat dibuat oleh Admin/Kader. |
| **BR-09** | Kader dapat membuat entri walk-in untuk anak yang belum terdaftar, selama tanggal posyandu terbuka dan tanggalnya <= hari ini. |
| **BR-10** | Setelah diukur, entri tidak dapat dibatalkan oleh orang tua. |
| **BR-11** | **Tidak ada jam buka tetap.** Pendaftaran dibuka hanya ketika kader membuka tanggal posyandu. |
| **BR-12** | Parent hanya dapat mendaftar jika tanggal posyandu `open` dan tanggalnya >= hari ini. |
| **BR-13** | Kader hanya dapat mengukur jika tanggal posyandu `open` dan tanggalnya <= hari ini. |
| **BR-14** | Maksimal satu tanggal posyandu `open` dalam satu waktu; satu tanggal hanya boleh memiliki satu baris. |
| **BR-15** | Satu instalasi hanya untuk Posyandu Bontoulu; tidak ada pemilihan posyandu. |
| **BR-16** | Tanggal yang `closed` tidak menerima pendaftaran baru dan tidak menerima pengukuran baru sampai dibuka kembali. |
| **BR-17** | Tanggal posyandu tidak boleh diubah setelah ada entri daftar hadir. |
| **BR-18** | Semua waktu menggunakan **WITA (`Asia/Makassar`)**. |
| **BR-19** | Pengukuran dan perubahan status daftar hadir disimpan dalam satu transaksi database. |
| **BR-20** | **Pengukuran hanya dapat dimulai dari entri daftar hadir.** Tidak ada pengukuran langsung dari profil anak. |
| **BR-21** | Walk-in selalu membuat entri daftar hadir terlebih dahulu, baru pengukuran. |
| **BR-22** | Anak yang sudah memiliki entri pada tanggal tersebut tidak boleh dibuatkan entri walk-in kedua. |

Validasi input pengukuran:

| Field | Wajib | Rentang wajar |
|---|---|---|
| Berat badan (BB) | Ya | 0,5–50 kg |
| Tinggi badan (TB) | Ya | 20–150 cm |
| Lingkar kepala (LK) | Tidak | 20–60 cm |
| Lingkar lengan (LILA) | Tidak | 5–30 cm |

---

## 10. Model Data

### 10.1 Skema yang ada saat ini

Berdasarkan kode aplikasi:

**`users`**
- `id_users` (uuid, terhubung ke Supabase Auth)
- `name`
- `role` (`Parent`, `Admin`)
- Data profil lain yang sudah ada.

**`children`**
- `id` (integer)
- `users_id` (uuid pemilik)
- `name`, `nik`, `gender`, `birth`
- `heightCm`, `weightKg`, `armCm`, `headCm`
- `created_at`

**`measurements`**
- `id` (integer)
- `children_id`
- `measured_at`
- `heightCm`, `weightKg`, `armCm`, `headCm`
- `created_at`

### 10.2 Perubahan skema

#### a. Tabel baru `posyandu_dates`

Tabel ini **bukan sesi**. Isinya hanya tanggal posyandu yang dibuka kader.

| Kolom | Tipe | Nullable | Default | Keterangan |
|---|---|---|---|---|
| `id` | bigint identity | Tidak | auto | Primary key |
| `posyandu_date` | date | Tidak | — | Tanggal posyandu |
| `status` | text | Tidak | `'open'` | `open` / `closed` |
| `opened_by` | uuid | Ya | null | `users.id_users` kader |
| `opened_at` | timestamptz | Tidak | `now()` | — |
| `closed_by` | uuid | Ya | null | `users.id_users` kader |
| `closed_at` | timestamptz | Ya | null | — |
| `created_at` | timestamptz | Tidak | `now()` | — |
| `updated_at` | timestamptz | Tidak | `now()` | — |

Constraint:
- `UNIQUE (posyandu_date)`
- `CHECK (status IN ('open','closed'))`
- Partial unique index untuk satu tanggal terbuka:
  ```sql
  CREATE UNIQUE INDEX posyandu_dates_single_open
  ON posyandu_dates (status)
  WHERE status = 'open';
  ```

#### b. Tabel baru `attendance_items`

| Kolom | Tipe | Nullable | Default | Keterangan |
|---|---|---|---|---|
| `id` | bigint identity | Tidak | auto | Primary key |
| `attendance_date` | date | Tidak | — | FK `posyandu_dates.posyandu_date` |
| `child_id` | integer | Tidak | — | FK `children.id` |
| `status` | text | Tidak | `'registered'` | `registered` / `measured` / `canceled` |
| `source` | text | Tidak | `'parent'` | `parent` / `kader` |
| `measurement_id` | integer | Ya | null | FK `measurements.id` |
| `created_at` | timestamptz | Tidak | `now()` | — |
| `updated_at` | timestamptz | Tidak | `now()` | — |

Constraint:
- `UNIQUE (attendance_date, child_id)`
- `CHECK (status IN ('registered','measured','canceled'))`
- `CHECK (source IN ('parent','kader'))`
- FK `attendance_date` → `posyandu_dates(posyandu_date)` ON UPDATE CASCADE ON DELETE CASCADE
- FK `child_id` → `children(id)` ON DELETE CASCADE
- FK `measurement_id` → `measurements(id)` ON DELETE SET NULL

Catatan:
- `parent_id` tidak disimpan karena dapat diturunkan dari `children.users_id`.
- Tidak ada `posyandu_id` (single-posyandu).
- Tidak ada `session_id`.

#### c. `children` — tambah kolom validasi

| Kolom | Tipe | Nullable | Default | Keterangan |
|---|---|---|---|---|
| `validation_status` | text | Tidak | `'pending'` | `pending` / `verified` / `rejected` |
| `verified_by` | uuid | Ya | null | `users.id_users` kader |
| `verified_at` | timestamptz | Ya | null | Waktu validasi |
| `verification_note` | text | Ya | null | Alasan penolakan/koreksi/import |

Migrasi data lama:
- Semua `children` yang sudah ada di-set `verified`.
- Data hasil import di-set `verified` dengan `verified_at = now()` dan `verification_note = 'import'`.
- Anak baru dari orang tua di-set `pending`.

#### d. `measurements` — tambah metadata

| Kolom | Tipe | Nullable | Default | Keterangan |
|---|---|---|---|---|
| `measure_date` | date | Tidak | `current_date` | Tanggal pengukuran menurut server (WITA) |
| `source` | text | Tidak | `'manual'` | `manual` / `device` / `migration` |
| `device_id` | text | Ya | null | Diisi fase 2 |
| `measured_by` | uuid | Ya | null | Kader yang mengukur |
| `updated_at` | timestamptz | Ya | null | Waktu koreksi |
| `updated_by` | uuid | Ya | null | Kader yang mengoreksi |

Constraint:
- `UNIQUE (children_id, measure_date)`
- `CHECK (source IN ('manual','device','migration'))`

Catatan:
- Pengukuran hasil import tidak memiliki entri daftar hadir.
- Pengukuran baru dari kader wajib memiliki entri daftar hadir pada `attendance_items`.
- `measure_date` dihitung dari tanggal server WITA.

#### e. Index

- `posyandu_dates (posyandu_date)`
- `attendance_items (attendance_date, status)`
- `attendance_items (child_id)`
- `measurements (children_id, measured_at)`
- `children (users_id)`

### 10.3 Zona waktu

- Zona waktu resmi: **WITA (`Asia/Makassar`)**.
- Database, server, dan aplikasi harus konsisten menggunakan WITA untuk menentukan "hari ini".
- Jangan menggunakan `LocalDate.now()` perangkat untuk menentukan tanggal posyandu atau tanggal pengukuran.
- Set zona waktu database:
  ```sql
  ALTER DATABASE postgres SET timezone = 'Asia/Makassar';
  ```

### 10.4 RPC transaksional yang disarankan

Operasi berikut sebaiknya dibuat sebagai fungsi database (RPC) agar atomik dan tidak dapat dilewati dari klien:

| RPC | Fungsi |
|---|---|
| `open_posyandu_date(p_date)` | Membuat tanggal posyandu terbuka; validasi satu tanggal aktif dan tanggal tidak lampau. |
| `update_posyandu_date(p_date_id, p_new_date)` | Mengubah tanggal selama belum ada entri. |
| `close_posyandu_date(p_date_id)` | Menutup pendaftaran. |
| `reopen_posyandu_date(p_date_id)` | Membuka kembali pendaftaran. |
| `register_attendance(p_date, p_child_ids[])` | Upsert entri untuk banyak anak. |
| `cancel_attendance(p_date, p_child_id)` | Membatalkan entri yang belum diukur. |
| `validate_child(p_child_id, ...)` | Menyimpan koreksi identitas dan set `verified`. |
| `add_walk_in(p_date, p_child_id)` | Membuat entri source kader. |
| `save_measurement(p_date, p_child_id, ...)` | Insert/koreksi pengukuran + update status entri dalam satu transaksi. |

---

## 11. Import Data Lama

Bagian ini wajib karena proyek baru akan menerima data yang sudah ada sebelumnya.

### 11.1 Ruang lingkup import

| Data | Masuk MVP | Keterangan |
|---|---|---|
| Akun orang tua | Ya | Dibuat/dihubungkan ke Supabase Auth. |
| Data anak | Ya | Otomatis `verified`. |
| Riwayat pengukuran | Ya | Dipertahankan tanggalnya. |
| Riwayat tanggal posyandu | Tidak | Tanggal baru dibuat setelah aplikasi berjalan. |
| Riwayat daftar hadir | Tidak | Tidak ada data historis yang perlu direkonstruksi. |
| Akun kader | Ya, terbatas | Dibuat oleh Super Admin/web, bukan dari Android. |

### 11.2 Aturan import

| ID | Aturan |
|---|---|
| **IMP-01** | Import dijalankan di lingkungan staging terlebih dahulu, bukan langsung produksi. |
| **IMP-02** | Backup database sebelum import. |
| **IMP-03** | Import harus idempotent: menjalankan ulang tidak membuat duplikat. |
| **IMP-04** | Data anak hasil import di-set `validation_status = 'verified'`, `verified_at = now()`, `verification_note = 'import'`. |
| **IMP-05** | `created_at` lama dipertahankan jika tersedia. |
| **IMP-06** | Riwayat pengukuran di-set `source = 'migration'`, `measured_by = null`. |
| **IMP-07** | `measure_date` dihitung dari `measured_at` (atau `created_at`) pada zona waktu WITA. |
| **IMP-08** | Jika satu anak memiliki lebih dari satu pengukuran pada tanggal yang sama, simpan yang paling baru dan catat yang dilewati di laporan. |
| **IMP-09** | Duplikat anak dideteksi berdasarkan NIK (jika ada), atau kombinasi orang tua + nama + tanggal lahir. |
| **IMP-10** | Duplikat akun orang tua dideteksi berdasarkan email. |
| **IMP-11** | Password tidak boleh diimpor sebagai teks biasa dan tidak boleh dicatat di log. Gunakan Supabase Auth Admin API, undangan, atau reset password. |
| **IMP-12** | Akun lama dengan password default wajib direset sebelum uji lapangan. |
| **IMP-13** | NIK dan data pribadi disamarkan pada laporan import. |
| **IMP-14** | Import berjalan per batch dan dapat diulang tanpa menggandakan data. |
| **IMP-15** | Setelah import, jalankan query verifikasi: jumlah data, anak tanpa orang tua, pengukuran tanpa anak, duplikat tanggal, dan rentang nilai tidak wajar. |

### 11.3 Urutan import

```
1. Akun auth orang tua (Supabase Auth)
2. Baris users (role = Parent)
3. Data children (verified)
4. Data measurements (source = migration)
5. Verifikasi dan laporan
```

### 11.4 Kunci idempotensi

| Tabel | Kunci |
|---|---|
| `users` | `email` |
| `children` | `users_id` + `nik` (jika NIK valid), atau `users_id` + `name` + `birth` |
| `measurements` | `children_id` + `measure_date` |

### 11.5 Laporan yang harus dihasilkan

- Jumlah akun dibuat / dilewati.
- Jumlah anak dibuat / dilewati / perlu review.
- Jumlah pengukuran dibuat / dilewati karena duplikat tanggal.
- Daftar anak tanpa orang tua.
- Daftar pengukuran tanpa anak.
- Daftar tanggal lahir kosong/tidak valid.
- Daftar NIK duplikat.
- Daftar nilai BB/TB/LK/LILA di luar rentang wajar.
- Jumlah total sebelum dan sesudah import.

### 11.6 Catatan penting

- Import **tidak** membuat data kehadiran historis. Riwayat kehadiran dimulai dari tanggal posyandu pertama setelah aplikasi berjalan.
- Data antropometri awal di `children` tidak otomatis disalin ke `measurements`. Jika ingin dijadikan titik awal grafik, keputusan dan aturannya dibahas di fase 2.
- Setelah import, semua anak lama sudah `verified`; kader tidak perlu memvalidasi ulang satu per satu.
- Anak baru yang didaftarkan orang tua setelah aplikasi berjalan tetap `pending` dan wajib divalidasi kader.

---

## 12. Otorisasi & RLS

### 12.1 Matriks RLS

| Tabel | Parent | Admin/Kader |
|---|---|---|
| `posyandu_dates` | SELECT tanggal `open` dan tanggal lampau yang diikuti | SELECT/INSERT/UPDATE semua |
| `children` | SELECT/INSERT milik sendiri (`users_id = auth.uid()`); UPDATE hanya jika belum `verified` | SELECT semua; INSERT (walk-in); UPDATE semua |
| `attendance_items` | SELECT milik sendiri; INSERT untuk anak sendiri pada tanggal `open` dan tanggal valid; UPDATE hanya `canceled` pada entri yang belum `measured` | SELECT/INSERT/UPDATE semua |
| `measurements` | SELECT untuk anak sendiri; tidak boleh INSERT/UPDATE/DELETE | SELECT semua; INSERT/UPDATE hanya dari entri daftar hadir |
| `users` | SELECT/UPDATE profil sendiri | SELECT data orang tua; tidak boleh mengubah role |

### 12.2 Aturan tambahan

- RLS harus aktif pada semua tabel.
- Service role key tidak boleh ada di aplikasi Android.
- Parent tidak boleh menulis kolom validasi (`validation_status`, `verified_by`, `verified_at`).
- Parent tidak boleh menulis `measurements`.
- Parent tidak boleh membuat tanggal posyandu atau mengubah statusnya.
- Uji RLS dengan dua akun Parent: A tidak boleh melihat anak atau daftar hadir B.
- Tulis data Parent melalui RPC agar kolom yang boleh diubah terbatas.
- Endpoint/query lama yang mengambil seluruh anak (`getAllChild`) harus difilter atau dihapus penggunaannya untuk Parent.

### 12.3 Contoh aturan penting

- Parent hanya bisa insert `attendance_items` jika:
  - `child_id` miliknya, dan
  - tanggal posyandu berstatus `open`, dan
  - `attendance_date >= current_date`.
- Admin hanya bisa insert `measurements` baru jika:
  - ada entri `attendance_items` untuk `child_id` dan `attendance_date = measure_date`, dan
  - tanggal posyandu berstatus `open`, dan
  - `attendance_date <= current_date`.
- Tanggal posyandu `closed` tidak dapat menerima entri baru atau pengukuran baru.

---

## 13. Kontrak Integrasi Web

Web sudah berjalan dan **tidak diubah dalam proyek ini**, tetapi Android harus menjaga kompatibilitas:

1. Nilai role `Parent` dan `Admin` tidak berubah.
2. Kolom lama `measurements` tetap diisi: `children_id`, `measured_at`, `heightCm`, `weightKg`, `armCm`, `headCm`, `created_at`.
3. Kolom baru (`measure_date`, `source`, `device_id`, `measured_by`, `updated_at`, `updated_by`) harus nullable atau memiliki default.
4. Setiap pengukuran dari daftar hadir wajib membuat baris `measurements`, agar dashboard dan ekspor puskesmas di web tetap akurat.
5. Data hasil import tetap terlihat sebagai riwayat pengukuran biasa; web tidak perlu tahu asal datanya.
6. `posyandu_dates` dan `attendance_items` adalah tabel baru; web boleh mengabaikannya di MVP.
7. Super Admin tidak memerlukan perubahan apa pun di Android.
8. Jika web menampilkan jumlah penimbangan, data `source = 'migration'` tetap dihitung sebagai riwayat. Kebijakan khusus baseline dibahas fase 2.

---

## 14. Kebutuhan Fungsional

### 14.1 Parent

| ID | Kebutuhan |
|---|---|
| FR-P-01 | Parent dapat membuka halaman "Daftar Hadir Posyandu". |
| FR-P-02 | Jika ada tanggal posyandu terbuka, halaman menampilkan tanggal dan semua anak miliknya. |
| FR-P-03 | Jika tidak ada tanggal terbuka, halaman menampilkan empty state dan tombol daftar tidak aktif. |
| FR-P-04 | Parent dapat memilih lebih dari satu anak dalam satu pengiriman. |
| FR-P-05 | Submit membuat atau memperbarui entri tanpa duplikat. |
| FR-P-06 | Parent dapat menambahkan anak lain pada tanggal yang sama. |
| FR-P-07 | Parent dapat membatalkan anak yang belum diukur selama pendaftaran masih berlaku. |
| FR-P-08 | Parent melihat status per anak: Terdaftar, Selesai, Dibatalkan. |
| FR-P-09 | Jika sudah diukur, tersedia aksi "Lihat Hasil". |
| FR-P-10 | Parent dapat menambah anak dan mengedit identitas selama belum `verified`. |
| FR-P-11 | Parent tidak dapat mengukur atau mengubah hasil pengukuran. |
| FR-P-12 | Parent dapat melihat tanggal posyandu terakhir yang diikuti secara read-only. |

### 14.2 Admin/Kader

| ID | Kebutuhan |
|---|---|
| FR-K-01 | Kader dapat membuka tanggal posyandu untuk hari ini atau tanggal mendatang. |
| FR-K-02 | Kader dapat mengubah tanggal selama belum ada entri. |
| FR-K-03 | Kader dapat menutup pendaftaran dan melihat peringatan jika masih ada yang belum diukur. |
| FR-K-04 | Kader dapat membuka kembali tanggal yang sudah ditutup. |
| FR-K-05 | Kader melihat daftar hadir tanggal aktif saja. |
| FR-K-06 | Ringkasan jumlah: terdaftar, belum diukur, sudah diukur, perlu validasi. |
| FR-K-07 | Daftar dikelompokkan per orang tua dan menampilkan anak-anaknya. |
| FR-K-08 | Kader dapat mencari nama orang tua/anak. |
| FR-K-09 | Kader dapat memfilter: semua, belum diukur, sudah diukur, perlu validasi. |
| FR-K-10 | Kader dapat memvalidasi identitas anak secara inline. |
| FR-K-11 | Kader dapat mengukur manual dari entri daftar hadir. |
| FR-K-12 | Kader dapat menambah walk-in, yang otomatis membuat entri daftar hadir. |
| FR-K-13 | Kader dapat mengoreksi pengukuran pada tanggal yang sama. |
| FR-K-14 | Kader melihat hasil setelah pengukuran tersimpan. |
| FR-K-15 | Kader tidak dapat mengukur anak `pending`/`rejected` tanpa validasi. |
| FR-K-16 | Kader tidak dapat mengukur sebelum tanggal posyandu. |
| FR-K-17 | Kader tidak dapat mengukur pada tanggal yang sudah ditutup. |
| FR-K-18 | Tidak ada akses ukur langsung dari profil anak; harus dari daftar hadir. |

### 14.3 Navigasi & menu

- Menu Parent: **Daftar Hadir Posyandu**, Statistik Pertumbuhan, Perjalanan Pertumbuhan, Informasi Kesehatan.
- Menu Kader: **Daftar Hadir Posyandu**, Statistik Pertumbuhan, Perjalanan Pertumbuhan, Informasi Kesehatan, Tambah Profil Anak, Buat Akun Orang Tua (opsional, sesuai kebijakan).
- Route menu ganda "Perjalanan Pertumbuhan" yang saat ini sama dengan "Statistik" harus diperbaiki.
- Label UI `Admin` → **Kader**.
- Home Parent menampilkan kartu/banner jika ada tanggal posyandu terbuka.
- Home Kader menampilkan status tanggal posyandu aktif dan ringkasan singkat.

---

## 15. Kebutuhan Non-Fungsional

| ID | Kebutuhan |
|---|---|
| NFR-01 | Waktu respons daftar hadir < 2 detik pada koneksi normal. |
| NFR-02 | Data sensitif (NIK, tanggal lahir) hanya dapat diakses pemilik dan kader. |
| NFR-03 | Tidak ada password atau token di log aplikasi. |
| NFR-04 | Aplikasi menampilkan pesan jelas saat offline; tidak menyimpan pengukuran setengah jadi. |
| NFR-05 | Semua operasi tulis bersifat idempotent terhadap submit ganda. |
| NFR-06 | Tanggal dan waktu konsisten menggunakan WITA (`Asia/Makassar`). |
| NFR-07 | Hanya boleh ada satu tanggal posyandu terbuka; dijaga oleh constraint database. |
| NFR-08 | Penyimpanan pengukuran dan perubahan status daftar hadir harus atomik. |
| NFR-09 | Aksesibilitas dasar: label tombol jelas, kontras memadai, target sentuh minimal 48 dp. |

---

## 16. Fase Implementasi

### Fase 1 — MVP

**Kader**
- Buka/tutup/buka kembali tanggal posyandu.
- Ubah tanggal sebelum ada entri.
- Daftar hadir tanggal aktif + ringkasan.
- Pencarian dan filter.
- Validasi inline.
- Pengukuran manual dari entri daftar hadir.
- Walk-in.
- Koreksi pengukuran tanggal yang sama.

**Parent**
- Lihat tanggal posyandu terbuka.
- Daftar hadir multi-anak.
- Status per anak.
- Pembatalan sebelum diukur.
- Lihat hasil.
- Lihat tanggal terakhir read-only.

**Platform**
- Migrasi skema (`posyandu_dates`, `attendance_items`, kolom validasi).
- RLS.
- RPC transaksional.
- Import akun, anak, dan riwayat pengukuran.
- Perbaikan bug penghalang di §18.

### Fase 2

- Pengukuran dengan alat (device) setelah protokol alat jelas.
- Offline queue dan sinkronisasi.
- Status `no_show`.
- Notifikasi hasil (in-app/push).
- Riwayat daftar hadir beberapa tanggal.
- Audit log perubahan identitas dan pengukuran.
- Baseline data antropometri awal.
- Jadwal berulang otomatis.

### Fase 3

- Multi-posyandu.
- Ekspor daftar hadir ke web.
- Statistik kehadiran dan cakupan penimbangan per tanggal.
- Integrasi lanjutan puskesmas.

---

## 17. Acceptance Criteria

| ID | Skenario | Kriteria |
|---|---|---|
| AC-01 | Belum ada tanggal terbuka, Parent membuka daftar hadir | Empty state tampil; tidak ada tombol daftar yang aktif. |
| AC-02 | Kader membuka tanggal mendatang | Parent melihat tanggal dan dapat mendaftar. |
| AC-03 | Kader membuka tanggal hari ini | Parent dapat mendaftar dan kader dapat mengukur. |
| AC-04 | Kader mencoba membuka tanggal kedua | Ditolak; harus menutup tanggal yang aktif terlebih dahulu. |
| AC-05 | Kader mencoba membuat tanggal yang sudah punya baris | Ditolak; arahkan ke tanggal yang ada. |
| AC-06 | Parent mendaftarkan 2 anak | Terbentuk 2 entri `registered` pada tanggal tersebut. |
| AC-07 | Parent submit ulang anak yang sama | Tidak ada duplikat; entri lama yang diperbarui. |
| AC-08 | Anak `pending` didaftarkan | Entri berhasil dibuat dan diberi label "Perlu Validasi". |
| AC-09 | Kader menekan "Ukur" pada anak `pending` | Muncul layar validasi; pengukuran tidak dapat disimpan sebelum validasi. |
| AC-10 | Kader menekan "Ukur" sebelum tanggal posyandu | Aksi tidak tersedia/ditolak dengan pesan jelas. |
| AC-11 | Kader menyimpan pengukuran valid | `measurements` bertambah 1 baris; entri menjadi `measured`; `measurement_id` terisi. |
| AC-12 | Kader menyimpan pengukuran kedua pada tanggal yang sama | Tidak membuat baris baru; mode koreksi memperbarui baris yang ada. |
| AC-13 | Parent membatalkan anak sebelum diukur dan pendaftaran masih terbuka | Status menjadi `canceled`; anak dapat didaftarkan lagi. |
| AC-14 | Parent membatalkan anak yang sudah diukur | Ditolak; tampil hasil pengukuran. |
| AC-15 | Parent membatalkan setelah pendaftaran ditutup | Ditolak. |
| AC-16 | Kader menambah walk-in | Entri `source = kader` dibuat pada tanggal aktif, lalu pengukuran tersimpan. |
| AC-17 | Kader menutup pendaftaran yang masih punya entri belum diukur | Muncul peringatan jumlah; setelah ditutup, pendaftaran dan pengukuran baru ditolak. |
| AC-18 | Kader membuka kembali tanggal yang ditutup | Status kembali `open`; pendaftaran/pengukuran mengikuti aturan tanggal. |
| AC-19 | Kader mengubah tanggal yang sudah punya entri | Ditolak; tanggal terkunci. |
| AC-20 | Parent A membuka daftar anak | Hanya anak milik A yang tampil; anak Parent B tidak tampil. |
| AC-21 | Data lama diimpor | Anak berstatus `verified`, riwayat pengukuran tampil, tidak ada duplikat saat import diulang. |
| AC-22 | Web membuka dashboard/ekspor | Data pengukuran baru dan hasil import terbaca; tidak ada error kolom. |
| AC-23 | Perangkat Parent menggunakan tanggal berbeda | Semua keputusan tanggal memakai tanggal server WITA. |
| AC-24 | Parent mencoba mengubah identitas anak `verified` | Aksi edit identitas tidak tersedia; perubahan hanya oleh kader. |
| AC-25 | Kader menutup tanggal lalu membuka tanggal baru | Tanggal baru dapat dibuka; tanggal lama tetap read-only. |
| AC-26 | Kader mencoba mengukur dari profil anak tanpa entri daftar hadir | Ditolak; kader diarahkan menambah walk-in terlebih dahulu. |
| AC-27 | Kader menambah walk-in untuk anak yang sudah punya entri | Ditolak; sistem menampilkan entri yang sudah ada. |

---

## 18. Prasyarat Teknis & Bug yang Harus Diperbaiki

Bug berikut berada di kode saat ini dan **menghalangi** alur MVP:

| ID | Lokasi | Masalah | Dampak |
|---|---|---|---|
| BUG-01 | `ui/components/AppTopBar.kt` | Blok `if (showSearch) { }` kosong | Pencarian orang tua di Tambah Anak dan Ukur tidak berfungsi; tombol simpan tetap disabled. |
| BUG-02 | `data/supabase/ChildService.kt` | `getAllChild()` mengambil semua anak | Parent dapat melihat anak orang lain. |
| BUG-03 | `DetailScreen` / `HistoryScreen` | Memakai daftar anak tanpa filter pemilik | Kebocoran data dan grafik salah. |
| BUG-04 | `ui/viewmodel/AuthViewModel.kt` | Log menampilkan data akun termasuk password | Risiko keamanan. |
| BUG-05 | `ui/screen/family/NewMeasureChildrenScreen.kt` | Input tanggal manual dan pengukuran tidak terikat daftar hadir | Tanggal tidak konsisten dan data bisa ganda. |
| BUG-06 | `data/model/MenuItem.kt` | Dua menu memakai route `detail` yang sama | "Perjalanan Pertumbuhan" membuka layar yang salah. |
| BUG-07 | `import_database/import.js` | Akun import memakai password default | Akun rentan; perlu reset/forced change. |
| BUG-08 | `children` vs `measurements` | Data awal tidak ada di `measurements` | Grafik kosong untuk anak lama. |
| BUG-09 | Alur ukur saat ini | Tidak ada konsep tanggal posyandu, validasi anak, dan walk-in | Pengukuran tidak terikat posyandu dan data belum terverifikasi. |
| BUG-10 | Akses ukur saat ini | Pengukuran bisa dimulai dari pencarian anak, bukan dari daftar hadir | Melanggar aturan BR-20 dan sulit diaudit. |

Perbaikan BUG-01 sampai BUG-06, BUG-09, dan BUG-10 wajib masuk sprint MVP. BUG-07 dan BUG-08 diselesaikan bersamaan dengan import data.

---

## 19. Risiko

| Risiko | Dampak | Mitigasi |
|---|---|---|
| Kader lupa menutup pendaftaran | Tanggal berikutnya tidak bisa dibuka | Banner pengingat + tombol tutup jelas; validasi satu tanggal terbuka. |
| Kader salah memilih tanggal | Orang tua mendaftar untuk tanggal salah | Tanggal bisa diubah sebelum ada entri; konfirmasi tanggal. |
| Tanggal lampau masih terbuka | Pendaftaran membingungkan | Blokir pendaftaran jika tanggal sudah lewat; minta kader menutup. |
| RLS salah konfigurasi | Data anak bocor | Uji dengan dua akun Parent; review policy sebelum rilis. |
| Kader salah memilih anak | Data masuk ke anak yang salah | Tampilkan identitas lengkap + usia sebelum konfirmasi ukur. |
| Submit ganda | Duplikat daftar hadir | Unique `(attendance_date, child_id)` + upsert. |
| Pengukuran ganda | Grafik ganda | Unique `(children_id, measure_date)` + mode koreksi. |
| Zona waktu salah | Tanggal pengukuran salah | WITA di server + tanggal server. |
| Import duplikat | Data ganda | Kunci idempotensi + laporan + staging. |
| Password default | Akun disalahgunakan | Reset password sebelum uji lapangan. |
| Kader terkendala koneksi | Tidak bisa mencatat | MVP online-only; siapkan prosedur kertas sementara. |
| Alat IoT belum siap | Janji fitur tidak terpenuhi | Keluarkan device dari MVP. |
| Data lama tidak lengkap | Grafik tidak bisa ditampilkan | Laporan import + verifikasi manual. |

---

## 20. Pertanyaan Terbuka & Keputusan yang Belum Dikunci

| ID | Pertanyaan | Rekomendasi |
|---|---|---|
| Q-01 | Apakah tanggal yang `closed` boleh dibuka kembali? | Ya, oleh kader, selama tidak ada tanggal lain yang terbuka. |
| Q-02 | Apakah tanggal boleh diubah setelah ada entri? | Tidak; kunci tanggal. Jika salah, tutup dan buat tanggal baru. |
| Q-03 | Apakah Parent boleh melihat tanggal terakhir yang sudah ditutup? | Ya, read-only, hanya tanggal yang diikuti. |
| Q-04 | Apakah koreksi pengukuran boleh mengubah baris yang ada? | Ya, update baris yang sama + catat `updated_by`/`updated_at`. |
| Q-05 | Apakah kader boleh mengubah identitas anak yang sudah `verified`? | Boleh, dengan catatan alasan; audit lengkap fase 2. |
| Q-06 | Apakah data antropometri awal perlu di-backfill sebagai baseline? | Tunda ke fase 2 setelah verifikasi sumber data. |
| Q-07 | Format NIK: wajib 16 digit atau boleh kosong? | Ikuti aturan yang sudah ada; validasi panjang jika diisi. |
| Q-08 | Apakah akun kader dibuat dari Android atau hanya web? | Hanya web/Super Admin; Android tidak membuat akun kader. |
| Q-09 | Apakah anak `rejected` boleh tetap tampil di daftar hadir? | Ya, dengan label jelas; tidak dapat diukur sampai diperbaiki. |
| Q-10 | Apakah satu tanggal boleh memiliki beberapa kader pengukur? | Ya; `measured_by` mencatat kader yang menyimpan. |
| Q-11 | Apakah pendaftaran otomatis ditutup pada akhir hari? | Tidak; kader menutup manual, dengan pengingat. |

---

## 21. Metrik Keberhasilan

| Metrik | Target awal |
|---|---|
| Anak terdaftar yang berhasil diukur pada tanggal yang sama | ≥ 80% |
| Duplikat entri daftar hadir | 0 |
| Duplikat pengukuran per anak per tanggal | 0 |
| Keluhan data anak terlihat oleh orang lain | 0 |
| Waktu kader menemukan anak di daftar | < 30 detik |
| Tanggal posyandu yang ditutup tepat waktu | ≥ 90% |

---

## 22. Lampiran — Teks UI yang Disarankan

| Konteks | Teks |
|---|---|
| Judul halaman Parent | **Daftar Hadir Posyandu** |
| Ada tanggal terbuka | "Posyandu: [tanggal]. Pilih anak yang akan dibawa." |
| Tidak ada tanggal | "Belum ada tanggal posyandu yang dibuka kader." |
| Catatan | "Ini bukan nomor antrian. Kader melayani sesuai kondisi di lapangan." |
| Tombol | **Daftarkan Anak** |
| Status | Terdaftar · Selesai · Dibatalkan · Perlu Validasi |
| Judul halaman Kader | **Daftar Hadir Posyandu** |
| Belum ada tanggal | "Belum ada tanggal posyandu. Buka pendaftaran untuk memulai." |
| Tombol kader | **Buka Pendaftaran Posyandu** · **Ukur** · **Validasi & Ukur** · **Lihat Hasil** · **Tambah Walk-in** |
| Tanggal mendatang | "Pendaftaran dibuka. Pengukuran tersedia mulai [tanggal]." |
| Tanggal lampau | "Tanggal posyandu sudah lewat. Tutup pendaftaran atau selesaikan pencatatan." |
| Tutup pendaftaran | "Masih ada [jumlah] anak belum diukur. Tutup pendaftaran tetap?" |
| Setelah diukur | "Pengukuran hari ini sudah tersimpan." |
| Tanggal terakhir Parent | "Tanggal posyandu terakhir: [tanggal] — hanya lihat." |
| Tombol ukur | **Ukur Anak** |

---

## 23. Ringkasan Keputusan yang Sudah Dikunci

1. Satu posyandu: **Posyandu Bontoulu**; tanpa `posyandu_id`.
2. Role: **Super Admin** (web saja), **Admin/Kader** (web + Android), **Parent** (Android).
3. Nilai role database tetap `Parent` dan `Admin`.
4. Ini **daftar hadir per tanggal posyandu**, bukan antrian bernomor dan bukan sesi bernomor.
5. **Tidak ada jam buka tetap 07:00 dan tidak ada sesi.** Kader membuka tanggal posyandu.
6. Orang tua hanya bisa mendaftar jika tanggal posyandu terbuka dan tanggalnya belum lewat.
7. Maksimal satu tanggal posyandu terbuka dalam satu waktu; satu tanggal hanya satu baris.
8. Kader hanya bisa mengukur pada tanggal terbuka dan tanggalnya sudah tiba/lewat.
9. Pengukuran hanya oleh kader.
10. **Pengukuran hanya dari entri daftar hadir; walk-in dibuat dulu oleh kader.**
11. Orang tua boleh mendaftarkan lebih dari satu anak.
12. Status disimpan per anak.
13. Satu anak = satu entri per tanggal; satu anak = satu pengukuran per tanggal.
14. Walk-in masuk MVP.
15. Validasi data anak dilakukan inline saat akan mengukur.
16. Data anak lama dan hasil import otomatis `verified`.
17. Import mencakup akun orang tua, anak, dan riwayat pengukuran; bukan riwayat kehadiran.
18. Pengukuran alat dan offline masuk fase 2.
19. Zona waktu: **WITA (`Asia/Makassar`)**.
20. Tanggal ditentukan server.
21. **Hanya satu tanggal posyandu aktif**, ditentukan kader; disarankan dibuka pada hari pelaksanaan atau H-1.

---

**Akhir dokumen — PRD v1.0 (Final)**
