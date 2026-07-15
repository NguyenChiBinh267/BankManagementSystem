# SmartBank Design System

## Principles

- Enterprise banking: clear hierarchy, restrained color, visible state changes.
- Data first: amounts, account identity, transaction direction, and errors remain easy to scan.
- Keyboard accessible: interactive controls keep a visible focus state.
- Stable layout: controls use fixed heights and panels fit a 1366x768 laptop viewport.
- No sensitive display: PIN fields are masked and PIN values are never shown in confirmations.

## Color Tokens

| Role | Value | Java token |
|---|---:|---|
| Primary | `#1E40AF` | `UIStyle.PRIMARY` |
| Primary hover | `#1D4ED8` | `UIStyle.PRIMARY_HOVER` |
| Background | `#F8FAFC` | `UIStyle.BACKGROUND` |
| Surface | `#FFFFFF` | `UIStyle.CARD_BACKGROUND` |
| Subtle surface | `#F1F5F9` | `UIStyle.SURFACE_SUBTLE` |
| Main text | `#0F172A` | `UIStyle.TEXT` |
| Muted text | `#475569` | `UIStyle.MUTED_TEXT` |
| Border | `#CBD5E1` | `UIStyle.BORDER` |
| Success | `#15803D` | `UIStyle.SUCCESS` |
| Warning | `#B45309` | `UIStyle.WARNING` |
| Error | `#B91C1C` | `UIStyle.ERROR` |

Semantic colors must be accompanied by text or an icon. Do not communicate status by color alone.

## Typography

The desktop system font is Segoe UI. Roles are defined in `UIStyle`:

- Display: 28 bold.
- Page title: 24 bold.
- Section title: 18 bold.
- Body: 14 regular.
- Field: 15 regular.
- Label and button: 13-14 bold.
- Supporting text: 12 regular.
- Money: 26 bold with locale-aware Vietnamese formatting.

## Spacing And Shape

- Spacing scale: `4, 8, 12, 16, 24, 32`.
- Control height: `42`.
- Control radius: `6`.
- Card and dialog radius: `8`.
- Sidebar width: `232`.
- Header height: `72`.

## Components

- `PrimaryButton`: one primary command per form; loading spinner disables repeated submission.
- `SecondaryButton`: navigation and supporting actions.
- `StyledTextField`: focus and error borders with a specific tooltip message.
- `PasswordField`: masked sensitive input with the same focus/error behavior.
- `SidebarItem`: icon, label, hover, keyboard focus, and active state.
- `BalanceCard`: primary account metric using tabular money formatting.
- `TransactionRow`: compact incoming/outgoing transaction summary.
- `StatusBadge`: text plus semantic status color.
- `ConfirmDialog`: modal confirmation and information variants.
- `NotificationPanel`: inline info, loading, success, warning, and error feedback.

Icons come from the dependency-free `SmartBankIcon` vector set. Structural emoji are not used.

## Layout

- `Main` is the only authenticated application frame.
- A persistent sidebar owns top-level navigation.
- A persistent header shows customer identity, masked card number, and logout.
- Main content uses `CardLayout`; transaction forms are dedicated `JPanel` classes.
- Registration uses a separate three-step `CardLayout` wizard.
- Legacy transaction frame classes are deprecated route adapters only.

## Data And Loading

- JDBC runs in `SwingWorker` through `SwingWorkerRunner`.
- Submit controls are disabled while work is active.
- Validation runs before opening a database connection.
- Transaction services recheck balance and account rules inside the database operation.
- Successful transactions update the visible balance and are available in history on refresh.
