# Xeno Plugin - Panduan Lengkap

## 🚀 Instalasi

### 1. Build Plugin
```bash
# Windows
gradlew.bat clean build

# Linux/Mac
./gradlew clean build
```

File hasil build akan ada di: **build/libs/Xeno-1.0.0.jar**

### 2. Upload ke Server
- Upload file **Xeno-1.0.0.jar** ke folder **/plugins/** di server
- **PENTING:** Jangan ekstrak file .jar, langsung upload!
- Restart server

### 3. Plugin Siap Digunakan
Plugin akan otomatis membuat folder `/plugins/Xeno/` dengan file-file config

---

## ✨ Fitur Lengkap

### 💰 **Sistem Ekonomi**
- Balance management per player
- Transfer uang antar player
- Integrasi Vault (compatible dengan plugin lain)
- Commands:
  - `/balance` atau `/bal` - Cek saldo
  - `/pay <player> <jumlah>` - Transfer uang
  - `/eco <give|take|set> <player> <jumlah>` - Admin

### 🛒 **Sistem Shop**
- GUI shop dengan buy/sell
- Config custom items
- Harga beli dan jual berbeda
- Commands:
  - `/shop` - Buka shop GUI
  - Klik kiri = beli
  - Klik kanan = jual

### 👥 **Sistem Role**
- Custom roles dengan permissions
- Priority system
- Commands:
  - `/role list` - List semua role
  - `/role set <player> <role>` - Set role player
  - `/role create <name>` - Buat role baru
  - `/role delete <name>` - Hapus role

### 🏠 **Sistem Home**
- Multiple homes per player
- Config max homes per role
- Commands:
  - `/sethome [name]` - Set home
  - `/home [name]` - Teleport ke home
  - `/delhome <name>` - Hapus home
  - `/homes` - List semua home

### 🌍 **Sistem Warp**
- Public warps untuk semua player
- Admin-only creation
- Commands:
  - `/warp <name>` - Teleport ke warp
  - `/setwarp <name>` - Buat warp (admin)
  - `/delwarp <name>` - Hapus warp (admin)
  - `/warps` - List semua warp

### 📍 **Sistem Teleport**
- TPA system dengan accept/deny
- Cooldown & warmup
- Commands:
  - `/tp <player>` - Direct TP (admin)
  - `/tpa <player>` - Request teleport
  - `/tpaccept` atau `/tpyes` - Terima request
  - `/tpdeny` atau `/tpno` - Tolak request

### 💬 **Private Messaging**
- PM antar player
- Reply system
- Commands:
  - `/msg <player> <pesan>` - Kirim pesan
  - `/reply <pesan>` atau `/r <pesan>` - Balas pesan

---

## ⚙️ Konfigurasi

### config.yml
```yaml
prefix: '&6[Xeno] &r'
currency-symbol: '$'
starting-balance: 1000.0

economy:
  max-balance: 1000000.0
  allow-negative: false

teleport:
  warmup: 3
  cooldown: 10
  cancel-on-move: true

homes:
  default-max: 3
```

### shops.yml
Edit untuk custom shop items:
```yaml
items:
  0:
    material: DIAMOND
    amount: 1
    buy-price: 100.0
    sell-price: 50.0
    display-name: '§bDiamond'
    slot: 10
```

### roles.yml
Edit untuk custom roles:
```yaml
default:
  priority: 0
  max-homes: 3
  permissions:
    - xeno.shop
    - xeno.home
```

---

## 🔧 Troubleshooting

### Plugin tidak load
- Cek Java version (min Java 8)
- Cek Spigot/Paper version (min 1.13)
- Lihat error di console/logs

### Economy tidak bekerja
- Install plugin Vault
- Restart server setelah install Vault

### Commands tidak work
- Cek permissions
- User butuh permission sesuai role

---

## 📋 Permissions

```
xeno.admin - Full admin access
xeno.admin.eco - Economy admin commands
xeno.admin.role - Role management
xeno.admin.tp - Direct teleport
xeno.admin.warp - Warp management
```

---

## 📞 Support

Jika ada masalah:
1. Cek console logs
2. Cek config.yml syntax
3. Pastikan semua dependency terinstall
4. Test dengan command `/balance` untuk verify plugin running

---

## 🎯 Default Setup Setelah Install

Setelah pertama kali dijalankan, plugin akan membuat:
- Default shop dengan 7 items
- Default role "default" 
- Starting balance Rp 1000 untuk player baru
- Folder data untuk player data storage

Semua bisa di-customize di file config!