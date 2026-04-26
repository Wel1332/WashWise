# WashWise Mobile — Architecture

This document describes the conventions the `washwise-mobile` app follows so new
screens are added in a consistent, testable way.

## 1. Vertical Slicing

Each user-facing feature lives under `com.washwise.mobile.feature.<slice>` and is
self-contained. A slice owns its own `data/`, `presenter/` and `ui/` sub-packages;
code from one slice should not import from another's `ui/` or `presenter/`.

```
com.washwise.mobile/
├── WashWiseApp.kt                  # Application class
├── feature/
│   ├── auth/
│   │   ├── data/                   # DTOs + AuthRepository
│   │   ├── presenter/              # LoginPresenter, RegisterPresenter
│   │   └── ui/                     # LandingActivity, LoginActivity, RegisterActivity
│   ├── dashboard/
│   │   ├── presenter/              # DashboardPresenter
│   │   └── ui/                     # DashboardFragment, ServiceAdapter
│   ├── order/
│   │   ├── data/                   # Order DTOs + OrderRepository
│   │   ├── presenter/              # Orders, BookService, OrderTracking presenters
│   │   └── ui/                     # OrdersFragment, BookServiceActivity, OrderTrackingActivity
│   ├── profile/
│   │   ├── data/                   # User DTOs + ProfileRepository
│   │   ├── presenter/              # Profile, UpdateProfile, ChangePassword presenters
│   │   └── ui/                     # ProfileFragment, UpdateProfileActivity, ChangePasswordActivity
│   └── service/
│       └── data/                   # ServiceResponse, ServiceRepository
├── shared/                         # Cross-slice infrastructure
│   ├── api/                        # ApiService (Retrofit), RetrofitClient
│   ├── model/                      # ApiResponse envelope
│   └── util/                       # SharedPrefManager, NetworkHelper, Constants
└── ui/main/                        # MainActivity shell hosting the bottom-tab fragments
```

Rule of thumb: **if two slices need the same code, pull it down into `shared/`.**
Never reach sideways.

## 2. MVP Design Pattern

Every non-trivial screen uses the Model-View-Presenter triad. The contract lives
in a single `*Contract.kt` file inside the slice's `presenter/` package.

### Contract

```kotlin
interface LoginContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun navigateToHome()
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun submit(email: String, password: String)
    }
}
```

### Presenter

- Pure JVM — **no Android imports**. That keeps it unit-testable in `src/test/`.
- Depends on a Repository from the same slice (or from `shared/`). Never on
  Retrofit directly.
- Owns a `CoroutineScope` backed by a `SupervisorJob` and cancels it in `detach()`.
- Holds a nullable reference to the View; every callback is a safe `view?.` call
  so a detached view never crashes the app.

### View

- `Activity` or `Fragment`. Holds the binding and routes input events to the
  presenter. Renders whatever the presenter pushes to it.
- Attaches the presenter in `onCreate` / `onViewCreated` and detaches it in
  `onDestroy` / `onDestroyView`.
- No network, no parsing, no business rules. Just state → view.

### Repository

- Wraps the Retrofit `ApiService`. Returns `kotlin.Result<T>` so the presenter
  can branch on `onSuccess` / `onFailure` without touching HTTP types.
- Normalizes the `{ success, message, data }` envelope into a flat Result.

## 3. Clean Code

- Functions are small and do one thing (ideally under 40 lines).
- Private helpers are below the public contract in each file; `// region View contract`
  tags mark the View-interface implementation block.
- No `!!`. Nullability is handled explicitly with `?.`, `?:` or `runCatching { ... }`.
- Strings shown to the user go through `res/values/strings.xml` when parameterized
  (e.g. `R.string.dashboard_greeting`).
- Magic numbers (price, min turnaround, max weight, password min length) live in a
  `companion object` or in `PresetService` — not scattered in activities.

## 4. Coding Conventions

- Kotlin: `PascalCase` for types, `camelCase` for properties/functions,
  `SCREAMING_SNAKE_CASE` for consts. File names match the top-level class.
- Package names are all lower-case (`feature.order.presenter`).
- XML resources:
  - Drawables: `bg_*` for backgrounds, `ic_*` for icons, `chevron_*` for chevrons.
  - Layouts: `activity_*` / `fragment_*` / `item_*`.
  - IDs: `btn*`, `tv*`, `et*`, `iv*`, `ll*`, `fl*`, `rv*`, `rowXxx` for menu rows.
- Contracts expose intent (`submit`, `load`, `onNewPasswordChanged`) — not
  transport (`callLoginApi`).

## 5. Adding a New Screen (Checklist)

1. Create `feature/<slice>/` (if the slice doesn't already exist) with `data/`,
   `presenter/`, `ui/`.
2. Add a `*Repository` under `data/` if the screen needs server data.
3. Write `*Contract.kt` with `View` + `Presenter` interfaces.
4. Implement `*Presenter` — pure Kotlin, depends on the repository.
5. Build the `Activity` / `Fragment` View implementing the contract. Keep it thin.
6. Register the activity in `AndroidManifest.xml`.
7. If you introduce new navigation strings or drawables, add them to
   `res/values/strings.xml` or `res/drawable/`.
