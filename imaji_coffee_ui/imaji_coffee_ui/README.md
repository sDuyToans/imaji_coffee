# Imaji Coffee UI

A modern, responsive web application for an online coffee shop built with cutting-edge technologies. This project provides a complete user interface for browsing coffee products, managing shopping carts, processing payments, and tracking orders.

## 🌟 Features

- **Product Catalog**: Browse and search through a variety of coffee products with detailed descriptions
- **Shopping Cart**: Add/remove items and manage cart contents seamlessly
- **User Authentication**: Secure login and registration system with JWT tokens
- **Payment Integration**: Support for multiple payment methods including Stripe and PayPal
- **Order Management**: Track order history and delivery status
- **Event Management**: Discover and participate in coffee-related events
- **News & Blog**: Stay updated with the latest coffee news and articles
- **Responsive Design**: Fully optimized for mobile, tablet, and desktop devices
- **Real-time Updates**: WebSocket support for live notifications and chat functionality
- **User Profile**: Manage personal information and preferences
- **Promotional System**: Apply discount codes and promo codes to orders

## 🛠️ Technologies Used

### Frontend Framework & Build Tools
- **[Vite](https://vitejs.dev/)** - Next generation frontend tooling with lightning fast HMR
- **[React 18.3.1](https://react.dev/)** - A JavaScript library for building user interfaces
- **[TypeScript](https://www.typescriptlang.org/)** - Typed superset of JavaScript for better code quality

### UI & Styling
- **[HeroUI](https://heroui.com/)** - Beautiful, accessible React components
- **[Tailwind CSS 4.1.11](https://tailwindcss.com/)** - Utility-first CSS framework
- **[Tailwind Variants 2.0.1](https://tailwind-variants.org/)** - CVA approach for Tailwind CSS
- **[Framer Motion 11.18.2](https://www.framer.com/motion/)** - Production-ready animations library

### State Management & Forms
- **[Redux Toolkit](https://redux-toolkit.js.org/)** - Efficient state management
- **[React Hook Form 7.62.0](https://react-hook-form.com/)** - Performant form management
- **[Yup 1.7.0](https://github.com/jquense/yup)** - Schema validation library

### Routing
- **[React Router DOM 6.23.0](https://reactrouter.com/)** - Client-side routing for single-page applications

### Payment & Commerce
- **[Stripe](https://stripe.com/)** - Payment processing integration
- **[PayPal SDK](https://developer.paypal.com/)** - PayPal payment gateway

### Real-time Communication
- **[React Use WebSocket 4.13.0](https://github.com/robtaussig/react-use-websocket)** - WebSocket integration
- **[STOMP.js 7.2.1](https://stomp-js.github.io/)** - STOMP protocol over WebSocket
- **[Socket.js Client 1.6.1](https://sockjs.org/)** - WebSocket fallback support

### UI Components & Icons
- **[React Icons 5.5.0](https://react-icons.github.io/react-icons/)** - Popular icon library
- **[Hero Icons 2.2.0](https://heroicons.com/)** - Beautiful hand-crafted SVG icons

### Media & Gallery
- **[Light Gallery 2.9.0-beta.1](https://www.lightgalleryjs.com/)** - Responsive image gallery
- **[Embla Carousel 8.6.0](https://www.embla-carousel.com/)** - Carousel/slider library
- **[React Justified Gallery 0.0.6](https://github.com/jiwonee/react-justified-gallery)** - Image gallery layout

### Notifications & UI Feedback
- **[React Hot Toast 2.6.0](https://react-hot-toast.com/)** - Toast notifications

### Utilities
- **[jwt-decode](https://github.com/auth0/jwt-decode)** - JWT token decoding
- **[React Phone Input 2 2.15.1](https://github.com/jquense/react-phone-input-2)** - Phone number input
- **[clsx 2.1.1](https://github.com/lukeed/clsx)** - Utility for constructing className strings
- **[Reselect 5.1.1](https://github.com/reduxjs/reselect)** - Selector library for Redux

### Development Tools
- **[ESLint](https://eslint.org/)** - JavaScript linter
- **[Prettier](https://prettier.io/)** - Code formatter
- **[PostCSS 8.5.6](https://postcss.org/)** - CSS transformation tool

## 📋 Prerequisites

Before you begin, ensure you have the following installed:
- **Node.js** (v16 or higher)
- **npm** (v7 or higher) or **yarn**
- **Git**

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/sDuyToans/imaji_coffee_ui
cd imaji-coffee-ui
```

### 2. Install Dependencies

Using npm:
```bash
npm install
```

Or using yarn:
```bash
yarn install
```

### 3. Environment Setup

Create a `.env` file in the root directory with the necessary environment variables:

```env
VITE_API_BASE_URL=http://localhost:3000/api
VITE_STRIPE_PUBLIC_KEY=your_stripe_public_key_here
```

> **Note**: Public keys like Stripe keys can be stored in `.env` files as they are not sensitive.

### 4. Run Development Server

Start the development server with hot module replacement:

```bash
npm run dev
```

The application will be available at `http://localhost:5173`

## 📦 Building for Production

### Build the Project

```bash
npm run build
```

This command will:
1. Run TypeScript type checking
2. Bundle the application using Vite
3. Generate optimized production files in the `dist/` directory

### Preview Production Build

To preview the production build locally:

```bash
npm run preview
```

### Build for Different Environments

- **Build for localhost**:
  ```bash
  npm run build:localhost
  ```

- **Build for development**:
  ```bash
  npm run build:dev
  ```

- **Build for production** (default):
  ```bash
  npm run build
  ```

## 🔍 Code Quality

### Linting

Fix ESLint issues automatically:

```bash
npm run lint
```

The project uses ESLint with Prettier integration for consistent code formatting.

## 📁 Project Structure

```
src/
├── api/                    # API endpoints and services
│   ├── account/           # User account endpoints
│   ├── auth/              # Authentication endpoints
│   ├── cart/              # Shopping cart endpoints
│   ├── events/            # Events endpoints
│   ├── news/              # News endpoints
│   ├── order/             # Order endpoints
│   ├── payment/           # Payment processing endpoints
│   ├── products/          # Product catalog endpoints
│   ├── promos/            # Promotional codes endpoints
│   └── ...
├── components/            # Reusable React components
│   ├── ui/               # UI primitive components
│   └── layouts/          # Layout components
├── features/              # Feature-specific modules
├── pages/                 # Page components
│   ├── home/             # Home page
│   ├── menu/             # Menu/Products page
│   ├── cart/             # Shopping cart page
│   ├── checkout/         # Checkout page
│   ├── auth/             # Authentication pages
│   ├── order/            # Order history page
│   ├── event/            # Events page
│   ├── news/             # News page
│   └── ...
├── routes/                # Route definitions
├── store/                 # Redux store configuration
├── context/               # React context providers
├── styles/                # Global styles
├── types/                 # TypeScript type definitions
├── utils/                 # Utility functions
├── hooks/                 # Custom React hooks
├── config/                # Configuration files
└── providers/             # Context and provider components
```

## 🔧 Key Configuration Files

- **vite.config.ts** - Vite bundler configuration with React plugin and Tailwind CSS
- **tailwind.config.js** - Tailwind CSS theme customization
- **tsconfig.json** - TypeScript compiler options
- **eslint.config.mjs** - ESLint configuration
- **postcss.config.js** - PostCSS configuration

## 📡 API Integration

The application is set up to connect to a backend API. Key API modules are located in `src/api/`:

- **Authentication** - User login, registration, and token management
- **Products** - Coffee product catalog
- **Cart** - Shopping cart operations
- **Orders** - Order creation and tracking
- **Payments** - Stripe and PayPal integration
- **Events** - Coffee events management
- **News** - Blog and news articles

## 💳 Payment Integration

### Stripe
The application integrates with Stripe for credit/debit card payments. Configure your Stripe public key in the `.env` file.

### PayPal
PayPal support is included for alternative payment options.

## 🔐 Security Notes

- JWT tokens are used for user authentication
- Sensitive backend endpoints are protected by authentication middleware
- Public API keys are safe to include in the frontend code
- Never commit private keys or sensitive credentials to version control

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support or questions, please open an issue in the repository or contact the project maintainers.

## 🙏 Acknowledgments

- [HeroUI](https://heroui.com) - For the beautiful component library
- [Tailwind CSS](https://tailwindcss.com) - For the utility-first CSS framework
- [Vite](https://vitejs.dev) - For the lightning-fast build tool
- All open-source contributors and libraries used in this project
