const { faker } = require('@faker-js/faker');

const CUSTOMER_COUNT = 100;
const ORDER_COUNT = 10_000;
const PRODUCT_COUNT = 200;

function escapeSql(value) {
    return String(value).replace(/'/g, "''");
}

function truncate(value, maxLength = 255) {
    return String(value).substring(0, maxLength);
}

function randomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function randomProductIds() {
    const count = randomInt(1, 5);
    const productIds = new Set();

    while (productIds.size < count) {
        productIds.add(randomInt(1, PRODUCT_COUNT));
    }

    return [...productIds];
}

// Generate customers
for (let i = 1; i <= CUSTOMER_COUNT; i++) {
    console.log(`INSERT INTO customer (id, name) VALUES (${i}, '${escapeSql(faker.name.fullName())}');`);
}

// Generate products
for (let i = 1; i <= PRODUCT_COUNT; i++) {
    const description = truncate(faker.commerce.productDescription());
    console.log(`INSERT INTO product (id, description) VALUES (${i}, '${escapeSql(description)}');`);
}

// Generate orders and assign 1-5 random products to each order
for (let i = 1; i <= ORDER_COUNT; i++) {
    const customerId = randomInt(1, CUSTOMER_COUNT);
    const name = truncate(faker.commerce.productName());
    console.log(`INSERT INTO "order" (id, description, customer_id) VALUES (${i}, '${escapeSql(name)}', ${customerId});`);

    randomProductIds().forEach((productId) => {
        console.log(`INSERT INTO order_products (order_id, product_id) VALUES (${i}, ${productId});`);
    });
}