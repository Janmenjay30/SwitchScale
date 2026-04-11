# darkstore_frontend

Manager UI for darkstore operations.

## Features

- View products by category
- Search products
- Add product
- Edit product and ETA
- Remove product

## Run

1. Start backend catalog service on `http://localhost:8002`
2. In this folder run:

```bash
npm install
npm run dev
```

3. Open `http://localhost:5180`

## API notes

- Uses Vite proxy path `/catalog-api` -> `http://localhost:8002`
- ETA is managed in browser local storage because current catalog model has no ETA field.
- Edit/Delete buttons call `PUT /products/{id}` and `DELETE /products/{id}`. If your backend does not expose these endpoints yet, the UI still applies the change locally and shows a warning.
