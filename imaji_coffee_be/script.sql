create table event
(
    event_id   bigint auto_increment
        primary key,
    name       varchar(255)                        not null,
    image      varchar(500)                        not null,
    start_date timestamp default CURRENT_TIMESTAMP not null,
    duration   time                                not null,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    created_by varchar(20)                         not null,
    updated_at timestamp                           null,
    updated_by varchar(20)                         null
);

create table news
(
    new_id      bigint auto_increment
        primary key,
    title       varchar(255)                        not null,
    description text                                null,
    image       varchar(500)                        not null,
    created_at  timestamp default CURRENT_TIMESTAMP not null,
    created_by  varchar(20)                         not null,
    updated_at  timestamp                           null,
    updated_by  varchar(20)                         null
);

create table orders
(
    order_id          bigint auto_increment
        primary key,
    user_id           bigint                                                                                                                              null,
    email             varchar(100)                                                                                                                        not null,
    shipping_address  json                                                                                                                                not null,
    total_amount      decimal(10, 2)                                                                                                                      not null,
    currency          varchar(3)                                                                                                                          not null,
    status            enum ('PENDING', 'PAYMENT_FAILED', 'PAID', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'REFUNDED') default 'PENDING'         null,
    payment_intent_id varchar(255)                                                                                                                        null,
    payment_method    varchar(50)                                                                                                                         null,
    created_at        timestamp                                                                                                 default CURRENT_TIMESTAMP not null,
    created_by        varchar(20)                                                                                                                         not null,
    updated_at        timestamp                                                                                                 default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    updated_by        varchar(20)                                                                                                                         null,
    tax_amount        decimal(10, 2)                                                                                            default 0.00              null,
    shipping_amount   decimal(10, 2)                                                                                            default 0.00              null,
    discount_amount   decimal(10, 2)                                                                                            default 0.00              null,
    shipping_method   varchar(100)                                                                                                                        null
);

create table products
(
    product_id          bigint auto_increment
        primary key,
    name                varchar(255)                                                                not null,
    description         text                                                                        null,
    price               decimal(10, 2)                                                              not null,
    old_price           decimal(10, 2)                                                              null,
    is_available_at_web tinyint(1)                                        default 1                 null,
    quantity            int                                               default 30                null,
    category            enum ('coffee_baverage', 'food_snack', 'at_home') default 'coffee_baverage' not null,
    created_at          timestamp                                         default CURRENT_TIMESTAMP not null,
    created_by          varchar(20)                                                                 not null,
    updated_at          timestamp                                                                   null,
    updated_by          varchar(20)                                                                 null
);

create table order_items
(
    order_item_id    bigint auto_increment
        primary key,
    order_id         bigint                                            not null,
    product_id       bigint                                            not null,
    product_name     varchar(255)                                      not null,
    price            decimal(10, 2)                                    not null,
    quantity         int                                               not null,
    created_at       timestamp default CURRENT_TIMESTAMP               not null,
    created_by       varchar(20)                                       not null,
    updated_at       timestamp default CURRENT_TIMESTAMP               null on update CURRENT_TIMESTAMP,
    updated_by       varchar(20)                                       null,
    product_category enum ('coffee_baverage', 'food_snack', 'at_home') not null,
    product_img      varchar(512)                                      not null,
    constraint order_items_ibfk_1
        foreign key (order_id) references orders (order_id)
            on delete cascade,
    constraint order_items_ibfk_2
        foreign key (product_id) references products (product_id)
);

create index idx_order_items_order_id
    on order_items (order_item_id);

create index order_id
    on order_items (order_id);

create index product_id
    on order_items (product_id);

create table product_images
(
    product_image_id bigint auto_increment
        primary key,
    product_id       bigint                               not null,
    image_url        varchar(500)                         not null,
    created_at       timestamp  default CURRENT_TIMESTAMP not null,
    created_by       varchar(20)                          not null,
    updated_at       timestamp                            null,
    updated_by       varchar(20)                          null,
    is_main          tinyint(1) default 0                 null,
    constraint product_images_ibfk_1
        foreign key (product_id) references products (product_id)
            on delete cascade
);

create index product_id
    on product_images (product_id);

create table promos
(
    id             bigint auto_increment
        primary key,
    code           varchar(50)                                   not null,
    title          varchar(255)                                  not null,
    description    text                                          null,
    discount_type  enum ('percentage', 'fixed', 'free_shipping') null,
    discount_value decimal(10, 2)                                null,
    start_at       timestamp                                     null,
    end_at         timestamp                                     null,
    is_active      tinyint(1) default 1                          null,
    created_at     timestamp  default CURRENT_TIMESTAMP          not null,
    created_by     varchar(20)                                   not null,
    updated_at     timestamp                                     null,
    updated_by     varchar(20)                                   null
);

create table promo_products
(
    promo_id   bigint                              not null,
    product_id bigint                              not null,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    created_by varchar(20)                         not null,
    updated_at timestamp                           null,
    updated_by varchar(20)                         null,
    primary key (promo_id, product_id),
    constraint promo_products_ibfk_1
        foreign key (promo_id) references promos (id),
    constraint promo_products_ibfk_2
        foreign key (product_id) references products (product_id)
);

create index product_id
    on promo_products (product_id);

create table roles
(
    role_id bigint auto_increment
        primary key,
    name    varchar(50) not null,
    constraint name
        unique (name)
);

create table ship
(
    method_id        bigint auto_increment
        primary key,
    method_name      varchar(100)                         not null,
    code             varchar(5)                           not null,
    expected_arrival varchar(100)                         not null,
    price            decimal(10, 2)                       not null,
    is_active        tinyint(1) default 1                 null,
    created_at       timestamp  default CURRENT_TIMESTAMP not null,
    created_by       varchar(20)                          not null,
    updated_at       timestamp                            null,
    updated_by       varchar(20)                          null,
    constraint code
        unique (code)
);

create table space
(
    space_id   bigint auto_increment
        primary key,
    name       varchar(255)                        not null,
    type       varchar(50)                         not null,
    image      varchar(500)                        not null,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    created_by varchar(20)                         not null,
    updated_at timestamp                           null,
    updated_by varchar(20)                         null
);

create table user
(
    user_id    bigint auto_increment
        primary key,
    email      varchar(100)                         not null,
    password   varchar(255)                         not null,
    created_at timestamp  default CURRENT_TIMESTAMP not null,
    created_by varchar(20)                          not null,
    updated_at timestamp                            null,
    updated_by varchar(20)                          null,
    user_name  varchar(255)                         not null,
    guest      tinyint(1) default 0                 null,
    constraint email
        unique (email),
    constraint user_name
        unique (user_name)
);

create table addresses
(
    address_id   bigint auto_increment
        primary key,
    user_id      bigint                               null,
    email        varchar(100)                         not null,
    full_name    varchar(100)                         not null,
    street       varchar(255)                         not null,
    city         varchar(100)                         not null,
    province     varchar(100)                         null,
    postal_code  varchar(20)                          null,
    country      varchar(2)                           not null,
    phone_number varchar(20)                          null,
    is_default   tinyint(1) default 0                 null,
    created_at   timestamp  default CURRENT_TIMESTAMP not null,
    created_by   varchar(20)                          not null,
    updated_at   timestamp  default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    updated_by   varchar(20)                          null,
    constraint addresses_ibfk_1
        foreign key (user_id) references user (user_id)
            on delete set null
);

create index idx_address_user_id
    on addresses (user_id);

create index idx_addresses_email
    on addresses (email);

create table cart
(
    cart_id        bigint auto_increment
        primary key,
    user_id        bigint                              not null,
    promo_id       bigint                              null,
    ship_method_id bigint                              null,
    created_at     timestamp default CURRENT_TIMESTAMP not null,
    created_by     varchar(20)                         not null,
    updated_at     timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    updated_by     varchar(20)                         null,
    constraint fk_cart_promo
        foreign key (promo_id) references promos (id),
    constraint fk_cart_ship
        foreign key (ship_method_id) references ship (method_id),
    constraint fk_cart_user
        foreign key (user_id) references user (user_id)
);

create table cart_item
(
    cart_item_id bigint auto_increment
        primary key,
    product_id   bigint                              not null,
    cart_id      bigint                              not null,
    quantity     int                                 not null,
    created_at   timestamp default CURRENT_TIMESTAMP not null,
    created_by   varchar(20)                         not null,
    updated_at   timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    updated_by   varchar(20)                         null,
    constraint fk_cart_item_cart_id
        foreign key (cart_id) references cart (cart_id),
    constraint fk_cart_item_product
        foreign key (product_id) references products (product_id),
    check (`quantity` > 0)
);

create table user_role
(
    user_id    bigint                              not null,
    role_id    bigint                              not null,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    created_by varchar(20)                         not null,
    updated_at timestamp                           null,
    updated_by varchar(20)                         null,
    primary key (user_id, role_id),
    constraint user_role_ibfk_1
        foreign key (user_id) references user (user_id)
            on delete cascade,
    constraint user_role_ibfk_2
        foreign key (role_id) references roles (role_id)
            on delete cascade
);

create index role_id
    on user_role (role_id);


