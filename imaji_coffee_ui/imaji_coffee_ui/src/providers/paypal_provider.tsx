import { PayPalScriptProvider } from "@paypal/react-paypal-js";

export default function PayPalProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  const initialOptions = {
    // clientID have no problem to public
    clientId:
      "Adud7pwtqQxhH5dqGrUZTK5aXlWDzeunxRYfqXaYMStLICVooMCwTyfu5PgLegEWItJDTeEyCwz8KJ-m",
    currency: "USD",
    intent: "capture",
  };

  return (
    <PayPalScriptProvider options={initialOptions}>
      {children}
    </PayPalScriptProvider>
  );
}
