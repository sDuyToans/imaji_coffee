import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import { Provider } from "react-redux";
import { Elements } from "@stripe/react-stripe-js";
import { loadStripe } from "@stripe/stripe-js";

import App from "./App.tsx";
import { HeroUiProvider } from "./providers/provider.tsx";
import "@/styles/globals.css";
import "@/styles/index.css";

import { store } from "@/store/store.ts";
import { CartProvider } from "@/context/cart.tsx";
import { PromoProvider } from "@/context/promo.tsx";

const stripePromise = loadStripe(
  "pk_test_51S3Tyy578Xxwj3ovk8MVYSqSgZd9zM0ftZEub6xiRkJmU6jpRxDmD4TiFvshn20APTSEadqSBerTWaUvNmuSseP600Jg5GL3zh",
); // public key. no problem to be watched

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <BrowserRouter>
      <Provider store={store}>
        <Elements stripe={stripePromise}>
          <HeroUiProvider>
            <CartProvider>
              <PromoProvider>
                <App />
              </PromoProvider>
            </CartProvider>
          </HeroUiProvider>
        </Elements>
      </Provider>
    </BrowserRouter>
  </React.StrictMode>,
);
