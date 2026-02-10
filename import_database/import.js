require('dotenv').config();

const { createClient } = require("@supabase/supabase-js");
const fs = require("fs");
const csv = require("csv-parser");
const { log } = require('console');

const supabase = createClient(process.env.SUPABASE_URL, process.env.SUPABASE_SERVICE_ROLE);

async function importData() {
    const results = []

    fs.createReadStream('./data/import-data-byname.csv')
        .pipe(csv())
        .on('data', (data) => results.push(data))
        .on('end', async () => {
            console.log(`🚀 Memulai impor ${results.length} data...\n`)

            let limit = 15
            let processed = 0

            for (const row of results) {
                // if (processed >= limit) {
                //     console.log('🛑 Limit test tercapai')
                //     break
                // }
                const rawNik = row.parent_nik?.trim() ?? ''

                const nik = rawNik ? rawNik
                    .toString()
                    .replace(/[^\d]/g, '') : null

                const rawkk = row.no_kk?.trim() ?? ''
                const kk = rawkk ? rawkk
                    .toString()
                    .replace(/[^\d]/g, '') : null
                const namaIbu = row.mom_name ?? null
                const namaAyah = row.father_name ?? null
                const rawPhone = row.phone?.trim() ?? ''
                const phone = rawPhone ? rawPhone
                    .toString()
                    .replace(/[^\d]/g, '') : null
                const address = row.address ?? null

                const namaAnak = row.child_name ?? null
                const rawBirth = row.child_birth ?? null

                const tanggalLahir = rawBirth ? rawBirth
                    .toString()
                    .replace(/[^\d-]/g, '') : null

                const rawNikAnak = row.child_nik?.trim() ?? ''
                const nikAnak = rawNikAnak ? rawNikAnak
                    .toString()
                    .replace(/[^\d]/g, '') : null

                const childNo = row.child_no ?? null
                const gender = row.child_gender ?? null

                const rawWeight = row.weightKg?.toString().trim() ?? ''
                const weight = rawWeight
                    ? (() => {
                        const n = parseFloat(
                            rawWeight.replace(',', '.').replace(/[^\d.]/g, '')
                        )
                        return isNaN(n) ? null : n
                    })()
                    : null

                const rawHeight = row.heightCm?.toString().trim() ?? ''
                const height = rawHeight
                    ? (() => {
                        const n = parseFloat(
                            rawHeight.replace(',', '.').replace(/[^\d.]/g, '')
                        )
                        return isNaN(n) ? null : n
                    })()
                    : null
                const rawHead = row.headCm?.toString().trim() ?? ''
                const head = rawHead
                    ? (() => {
                        const n = parseFloat(
                            rawHead.replace(',', '.').replace(/[^\d.]/g, '')
                        )
                        return isNaN(n) ? null : n
                    })()
                    : null

                let parentId = null
                const emailShadow = `${namaIbu}@bontoulu.com`

                console.log(emailShadow)

                // 1️⃣ CEK NIK DI PROFILES
                const { data: existingProfile } = await supabase
                    .from('users')
                    .select('id_users')
                    .eq('name', namaIbu)
                    .maybeSingle()

                if (existingProfile) {
                    console.log(`[PROFILE ADA] NAMA IBU ${namaIbu}`)
                    parentId = existingProfile.id_users
                } else {
                    console.log(`[PROFILE BARU] Membuat akun untuk NAMA IBU ${namaIbu}`)

                    // 2️⃣ BUAT AKUN AUTH
                    const { data: authData, error: authError } =
                        await supabase.auth.admin.createUser({
                            email: emailShadow,
                            password: 'dusunbontoulu'
                        })

                    if (authError) {
                        console.error(`[GAGAL AUTH] ${namaIbu}: ${authError.message}`)
                        continue
                    }

                    parentId = authData.user.id
                    parentEmail = authData.user.email

                    // 3️⃣ INSERT PROFILE
                    const { error: profileError } = await supabase
                        .from('users')
                        .insert({
                            id_users: parentId,
                            kk: kk,
                            nik: nik,
                            name: namaIbu,
                            father_name: namaAyah,
                            email: parentEmail,
                            phone: phone,
                            address: address,
                            is_example_email: true,
                            is_whitout_nik: true
                        })

                    if (profileError) {
                        console.error(`[GAGAL PROFILE] ${namaIbu}: ${profileError.message}`)
                        continue
                    }

                    console.log(`[PROFILE OK] ${namaIbu}`)
                }

                // 4️⃣ INSERT DATA ANAK
                const { error: childError } = await supabase
                    .from('children')
                    .insert({
                        users_id: parentId,
                        name: namaAnak,
                        birth: tanggalLahir,
                        nik: nikAnak,
                        gender: gender,
                        weightKg: weight,
                        heightCm: height,
                        headCm: head,
                        child_no: childNo
                    })

                if (childError) {
                    console.error(`[GAGAL ANAK] ${namaAnak}: ${childError.message}`)
                } else {
                    console.log(`[ANAK OK] ${namaAnak}`)
                }

                // processed++
            }

            console.log('\n✅ Proses impor selesai')
        })
}

importData()