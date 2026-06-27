# EezyGuest

**Find, request, and pay for verified PG and guest-room stays — built for students and room owners.**

EezyGuest (package: `com.example.bookandpostroom`) is an Android marketplace for long-stay room rentals. Students browse verified listings, submit booking requests with identity details, and pay first-month rent in-app. Owners post rooms, verify their bank accounts, approve tenants, and receive payouts through Razorpay split transfers.

The app is region-focused on **Kashmir districts** (Anantnag, Srinagar, Baramulla, and others) and targets the student PG / guest-room market rather than hotels or short-term vacation rentals.

Unique Real-Time Booking Queue System
The core innovation of this app is a fair, ordered transaction queue that handles simultaneous booking attempts intelligently — especially useful for shared PG rooms and high-demand listings.
How it works:

When multiple tenants (e.g., 4 users) submit a booking request for the same room at the same time, they are automatically entered into an ordered queue instead of causing race conditions.
The first user in the queue gets an exclusive timed action window (configurable, e.g., 6 seconds) to complete required actions (upload Aadhaar/details, etc.).
If they succeed within the time limit, the booking proceeds and remaining capacity is updated (e.g., 4-bed room → 1 bed left).
If they timeout or fail, they are automatically removed from the queue and the next user gets their turn instantly.
Real-time updates are pushed to all waiting users showing their current position and estimated wait.
Smart Shared Room Logic: The system intelligently handles different request sizes. For example:
A user requesting 3 beds is processed only if sufficient capacity remains.
If not, they can be offered partial matches or prompted to adjust (e.g., select 1 bed).
Users wanting fewer beds may get priority in certain scenarios, and the queue continues processing until it empties or the room is full.


This creates a transparent, first-come-first-served experience with built-in fairness, prevents double bookings, and significantly improves user trust compared to traditional race-condition-prone booking systems.
---

## Table of Contents

- [Who It's For](#who-its-for)
- [Core Features](#core-features)
- [How a Booking Works](#how-a-booking-works)
- [How It's Different From Similar Apps](#how-its-different-from-similar-apps)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Firebase Data Model](#firebase-data-model)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Cloud Functions](#cloud-functions)
- [Security Notes](#security-notes)

---

## Who It's For

### Students / Tenants
- Browse verified room listings
- Send booking requests with Aadhaar, photos, dates, gender, and headcount
- Pay approved bookings within 24 hours
- Track payments, refunds, and booking history
- Save favorites and leave reviews after staying

### Room Owners
- Post rooms with photos, pricing, facilities, rules, and map location
- Complete bank account setup for payouts
- Approve or reject tenant requests
- Manage occupied rooms and incoming payments
- Receive split payouts via Razorpay linked accounts

### Authentication
- **Google Sign-In only** (Firebase Auth + Android Credential Manager)
- Each account chooses **one role at signup**: `owner` or `student`
- Dual-role accounts are not supported — users must pick a single persona

---

## Core Features

### For Students
| Feature | Description |
|---------|-------------|
| Room discovery | Paginated feed of verified owner listings |
| Search & filters | District, institution, and location-based search |
| Favorites | SQLite-backed local favorites list |
| Booking requests | Aadhaar, photos, check-in/out dates, gender, capacity |
| In-app payments | Razorpay checkout for approved requests |
| Payment history | Transaction status, receipts, refund tracking |
| Reviews | Prompted ~20 days after payment via background worker |
| Refunds | 2-day cancellation window with policy shown at checkout |

### For Owners
| Feature | Description |
|---------|-------------|
| Room posting | Photos, GPS/manual location, facilities, pricing, capacity |
| Bank verification | Account number / IFSC setup; admin verifies before listing goes live |
| Request management | Approve/reject tenants with real-time notifications |
| Tenant details | View tenant Aadhaar and documents (screenshot-protected screens) |
| Payment dashboard | Received transactions and transfer status |
| Room management | Edit, update photos, manage occupied rooms |
| Map view | Mapbox satellite view of room location |

### Platform Features
- Push notifications (FCM) for requests, approvals, payments, and changes
- Receipt generation for owners and tenants
- Privacy policy, contact, and feedback screens
- WorkManager jobs for delayed reviews and document cleanup

---

## How a Booking Works

```
Student browses rooms
        ↓
Opens room details → submits booking request (Aadhaar + photos + dates)
        ↓
Request enters per-room queue (Firebase Realtime Database transaction)
        ↓
Owner receives push notification
        ↓
Owner approves request
        ↓
Student has 24 hours to pay via Razorpay
        ↓
Payment succeeds → booking confirmed → owner receives split payout
        ↓
Review prompt scheduled 20 days later
```

### Payment calculation
- Total = `(room price × number of spots) + 18% GST`
- Razorpay Route sends the owner's share to their verified linked account
- If a room becomes unavailable during payment, a refund flow is triggered automatically

### Refund policy
- Tenants can cancel within **2 days** of payment
- **18% GST is deducted** on cancellation (shown in the payment screen)
- Refund status is tracked in `pendingrefunds`

---

## How It's Different From Similar Apps

Most room-rental apps (NoBroker, Airbnb, MagicBricks, local PG listing sites) follow one of these patterns: instant contact, broker-mediated leads, or short-stay hotel-style booking. EezyGuest takes a different approach.

| Typical rental / PG apps | EezyGuest |
|--------------------------|-----------|
| Call or chat owner directly | Structured **request → approve → pay** pipeline |
| Instant or broker-led booking | Owner must approve; tenant pays within **24 hours** |
| Generic national listings | **Kashmir district-focused** with institution search |
| Listings go live immediately | Rooms appear only after **bank setup + admin verification** |
| Payment happens off-platform | **In-app Razorpay payment** with split settlement to owner |
| Simple "first come" booking | **Firebase transaction queues** with owner priority locking |
| Minimal tenant verification | **Aadhaar + photo upload** on booking requests |
| Reviews anytime | **Delayed review prompt** 20 days post-payment |
| Dual buyer/seller accounts common | **Single-role accounts** (owner OR student, not both) |
| Coordinates shown precisely | Map coordinates **rounded**; sensitive screens use `FLAG_SECURE` |
| Heavy local caching | Cloud-first RTDB; SQLite used only for favorites |

### What makes the architecture distinctive

1. **Queue-based concurrency** — Uses Firebase RTDB transactions for `queue`, `priorityqueue`, and `paymentbeingmade` to prevent race conditions when multiple tenants request or pay for the same room. Owners get absolute priority during accept/payment operations.

2. **Verification gates** — A room only appears in the student browse feed when both `accountsetup = 1` and `accountverified = 1`.

3. **Split payouts** — Payments are not just collected; they are routed to owner linked accounts via Razorpay Cloud Functions.

4. **Gender-aware capacity** — Rooms track booked gender; tenants specify gender during booking.

5. **Student-first design** — Built around PG/guest-room monthly rent, not nightly hotel stays.

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Java |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 34 |
| Build | Gradle Kotlin DSL, AGP 8.6 |
| UI | Material Components, Bottom Navigation, Drawer, ViewPager2, Lottie, Shimmer |
| Backend | Firebase Realtime Database (primary data store) |
| Auth | Firebase Auth + Google Sign-In (Credential Manager) |
| Storage | Firebase Storage (room and tenant images) |
| Messaging | Firebase Cloud Messaging |
| Functions | Firebase Cloud Functions (Node.js 22) |
| Payments | Razorpay Checkout 1.6.37 with Route/split transfers |
| Maps | Mapbox Android SDK 10.14.0 |
| Images | Glide |
| Local DB | SQLite (`MyDatabaseHelper` — favorites only) |
| Background work | WorkManager (`ReviewWorker`, `DeleteDocumentWorker`) |
| Networking | Volley, HTTP calls to Cloud Functions |

---

## Project Structure

```
BookAndPostRoom/
├── app/                              # Android application module (canonical source)
│   ├── build.gradle.kts
│   ├── google-services.json          # Gitignored — copy from .example
│   ├── google-services.json.example
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── assets/
│       │   ├── firebase-service-account.json.example
│       │   └── lottieanimation.json
│       ├── java/com/example/bookandpostroom/   # 90+ activity/fragment classes
│       └── res/
├── functions/                        # Firebase Cloud Functions
│   ├── index.js
│   └── package.json
├── firebase.json
├── .firebaserc
├── secrets.properties.example        # Razorpay, Mapbox tokens
├── keystore.properties.example       # Signing config
├── gradle.properties                 # Mapbox downloads token
└── *.java (root level)               # Legacy duplicates — NOT compiled by Gradle
```

> **Note:** There are ~86 `.java` files at the project root that mirror `app/src/main/java/`. Gradle only compiles sources under `app/src/`. Treat root-level files as legacy copies and prefer editing files inside `app/src/main/java/`.

### Key entry points

| File | Role |
|------|------|
| `MainActivity` | Splash screen + Google sign-in + role routing |
| `Activity1` | New user registration |
| `Activity2` | Role selection (owner vs student) |
| `Activity5` | Owner dashboard |
| `studentinfoandrooms` | Student dashboard |
| `bookActivity` | Booking request form |
| `gpayActivity` | Razorpay payment flow |
| `requestsActivityforowner` | Owner request approval |
| `fragmentforhome33` | Student room browse feed |
| `addActivity` / `Activity6` | Owner room creation/editing |

---

## Firebase Data Model

Simplified Realtime Database structure:

```
google/{displayName}/{uid}/
  ├── owner/
  │   ├── rooms/roomId:{compositeKey}/
  │   │   ├── queue, priorityqueue, paymentbeingmade
  │   │   ├── roomprice, roomcapacity, grabbedby, reviews/
  │   │   └── ...
  │   ├── actualrequestsrecieved/
  │   ├── accountdetails/          # Razorpay linked account
  │   ├── accountsetup, accountverified
  │   └── fcmtoken, transactionsrecievedno/
  └── student/
      ├── request data, transactionmadeno/, history/
      └── refundinfo, requestforchangeofprice/

rooms/                    # Global index for student browse feed
pendingaccounts/          # Owner bank verification queue
pendingpayments/          # Post-payment processing
pendingrefunds/           # Refund processing
```

Firebase project: `bookandpostroom-ecf7e`

---

## Getting Started

### Prerequisites
- Android Studio (latest stable)
- JDK 8+
- Firebase project with Realtime Database, Auth, Storage, Messaging, and Functions enabled
- Razorpay account with Route/linked accounts configured
- Mapbox account (runtime + downloads tokens)

### 1. Clone and open
```bash
git clone https://github.com/zeeshan47954/eezyGuest.git
cd eezyGuest
```
Open the project in Android Studio and let Gradle sync.

### 2. Create secret files
```bash
cp secrets.properties.example secrets.properties
cp keystore.properties.example keystore.properties
cp app/google-services.json.example app/google-services.json
cp app/src/main/assets/firebase-service-account.json.example \
   app/src/main/assets/firebase-service-account.json
```

Fill in real values in each file. These are gitignored and must never be committed.

### 3. Configure Gradle properties
Edit `gradle.properties`:
```properties
MAPBOX_DOWNLOADS_TOKEN=your_mapbox_downloads_token
```

### 4. Fill in `secrets.properties`
```properties
RAZORPAY_KEY_ID=your_key_id
RAZORPAY_KEY_SECRET=your_key_secret
RAZORPAY_TRANSFER_KEY_ID=your_transfer_key_id
RAZORPAY_TRANSFER_KEY_SECRET=your_transfer_key_secret
MAPBOX_ACCESS_TOKEN=your_mapbox_access_token
```

### 5. Build and run
```bash
./gradlew assembleDebug
```
Or use **Run** in Android Studio with a device/emulator (API 24+).

---

## Configuration

| File | Purpose |
|------|---------|
| `secrets.properties` | Razorpay keys, Mapbox access token → injected via `BuildConfig` |
| `keystore.properties` | Optional debug/release signing credentials |
| `gradle.properties` | Mapbox Maven downloads token |
| `app/google-services.json` | Firebase Android configuration |
| `app/src/main/assets/firebase-service-account.json` | FCM HTTP v1 credentials for push sending |

---

## Cloud Functions

The app calls deployed Cloud Functions (e.g. `createRazorpayOrder`, `sendFcmMessage`) at:

```
https://us-central1-bookandpostroom-ecf7e.cloudfunctions.net/
```

The local `functions/index.js` is currently a stub. Production functions must be deployed separately:

```bash
cd functions
npm install
firebase deploy --only functions
```

---

## Security Notes

- Never commit `secrets.properties`, `keystore.properties`, `google-services.json`, or `firebase-service-account.json`
- Rotate any keys that were previously exposed in source control
- Tenant identity screens use `FLAG_SECURE` to discourage screenshots
- Owner bank details go through an admin verification queue before listings go live

---

## License

No license file is included. Add one before open-source distribution.

---

## Contributing

1. Edit sources under `app/src/main/java/` only (not root-level `.java` duplicates)
2. Keep secrets in `.example` templates — never hardcode keys in source
3. Test both owner and student flows before submitting changes
