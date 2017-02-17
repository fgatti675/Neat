package com.neat.model;

import com.neat.model.classes.Item;
import com.neat.model.classes.Menu;
import com.neat.model.classes.MenuSection;
import com.neat.model.classes.Restaurant;

import java.util.UUID;


/**
 * Created by f.gatti.gomez on 10/10/16.
 */public class RestaurantProviderMock extends RestaurantProvider{

    private static final String TAG = "RestaurantProvider";

    public void getRestaurant(final String restaurantSlug, final Callback callback) {

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        callback.onRestaurantLoaded(getLateral());

    }

    public static Restaurant getLateral() {

        Restaurant restaurant = new Restaurant();

        restaurant.id = "lateral-test";
        restaurant.title = "Lateral";
        restaurant.subtitle = "Vive la experiencia";
        restaurant.headerUrl = "http://www.lateral.com/wp-content/uploads/2015/04/cabecera-carta.jpg";

        Menu menu = new Menu();
        menu.currency = "EUR";

        MenuSection drinks = new MenuSection();
        drinks.type = MenuSection.Type.small;
        drinks.headline = "¿Tenéis sed?";
        drinks.title = "Bebidas";
        drinks.subtitle = "Nuestros clientes suelen pedir estas bebidas, pero tenemos muchas más";
        MenuSection beers = new MenuSection();
        beers.title = "Cervezas";
        Item e0 = new Item(UUID.randomUUID().toString(), "Copa de Mahou", "31cl.", 2.70F, "EUR", "glass_small", null);
        beers.items.add(e0);
        beers.items.add(new Item(UUID.randomUUID().toString(), "Doble de Mahou", "50cl.", 4.50F, "EUR", "brandy_glass", null));
        Item e1 = new Item(UUID.randomUUID().toString(), "Copa Laiker", "Sin alcohol", 2.70F, "EUR", "glass_small_alt", null);
        beers.items.add(e1);
        beers.items.add(new Item(UUID.randomUUID().toString(), "1/3 Mahou negra", null, 3.35F, "EUR", "beer_bottle", null));
        beers.items.add(new Item(UUID.randomUUID().toString(), "1/3 Mahou mixta", null, 2.90F, "EUR", "beer_bottle", null));
        Item e4 = new Item(UUID.randomUUID().toString(), "1/3 Mahou light", null, 2.90F, "EUR", "beer_bottle", null);
        beers.items.add(e4);
        beers.items.add(new Item(UUID.randomUUID().toString(), "1/3 Carlsberg", null, 2.90F, "EUR", "beer_bottle", null));
        Item e = new Item(UUID.randomUUID().toString(), "1/3 Alhambra", null, 3.00F, "EUR", "beer_bottle", null);
        beers.items.add(e);
        drinks.subsections.add(beers);
        MenuSection vines = new MenuSection();
        vines.title = "Vinos";
        MenuSection riojas = new MenuSection();
        riojas.title = "Riojas";
        riojas.items.add(new Item(UUID.randomUUID().toString(), "Casa Viña Real Reserva", null, 2.70F, "EUR", "wine_glass", null));
        Item e2 = new Item(UUID.randomUUID().toString(), "Casa Viña Real Reserva", "50cl.", 9.95F, "EUR", "wine_glasses", null);
        riojas.items.add(e2);
        riojas.items.add(new Item(UUID.randomUUID().toString(), "Casa Viña Real Reserva", "75cl.", 13.45F, "EUR", "wine_glasses", null));
        riojas.items.add(new Item(UUID.randomUUID().toString(), "Contino Gran Reserva", "75cl.", 28.0F, "EUR", "wine_glasses", null));
        riojas.items.add(new Item(UUID.randomUUID().toString(), "Copa tinto de verano", null, 2.85F, "EUR", "glass_small_alt", null));
        vines.subsections.add(riojas);
        MenuSection riberas = new MenuSection();
        riberas.title = "Ribera del Duero";
        riberas.items.add(new Item(UUID.randomUUID().toString(), "Viña Solorca Reserva", null, 3.35F, "EUR", "wine_glass", null));
        vines.subsections.add(riberas);
        MenuSection ruedas = new MenuSection();
        ruedas.title = "Rueda";
        Item e3 = new Item(UUID.randomUUID().toString(), "Palacio de Bornos", null, 2.60F, "EUR", "wine_glass", null);
        ruedas.items.add(e3);
        ruedas.items.add(new Item(UUID.randomUUID().toString(), "Palacio de Bornos", null, 12.60F, "EUR", "wine_glasses", null));
        vines.subsections.add(ruedas);
        drinks.subsections.add(vines);
        menu.sections.add(drinks);

        drinks.featuredItems.add(e);
        drinks.featuredItems.add(e0);
        drinks.featuredItems.add(e4);
        drinks.featuredItems.add(e1);
        drinks.featuredItems.add(e2);
        drinks.featuredItems.add(e3);

        MenuSection featured = new MenuSection();
        featured.headline = "Hoy te recomendamos";
        featured.subtitle = "Disfruta con lo que hemos preparado hoy especial para ti";
        featured.type = MenuSection.Type.featured;
        Item p1 = new Item(UUID.randomUUID().toString(), "Arroz negro con chipirones", null, 3.60F, "EUR", "octopus", "http://www.lateral.com/wp-content/uploads/2015/05/Arroz-negro-con-chipirones.jpeg");
        Item p2 = new Item(UUID.randomUUID().toString(), "Magret de Pato con Chutney de Piña y Mango", null, 3.90F, "EUR", "duck", "http://www.lateral.com/wp-content/uploads/2015/05/Magret-de-Pato-con-Chutney-de-Pin%CC%83a-y-Mango.jpg");
        Item p3 = new Item(UUID.randomUUID().toString(), "Morcilla con patata paja con yema de huevo de corral", null, 4.00F, "EUR", "eggs", "http://www.lateral.com/wp-content/uploads/2015/05/Morcilla-con-patata-paja-con-yema-de-huevo-de-corral.jpeg");
        featured.items.add(p1);
        featured.items.add(p2);
        featured.items.add(p3);
        menu.sections.add(featured);

        MenuSection pinchos = new MenuSection();
        pinchos.type = MenuSection.Type.list;
        pinchos.title = "Pinchos";
        pinchos.items.add(p1);
        pinchos.items.add(p2);
        pinchos.items.add(p3);
        pinchos.items.add(new Item(UUID.randomUUID().toString(), "Picaña de ternera", null, 4.20F, "EUR", "steak", "http://www.lateral.com/wp-content/uploads/2015/05/Pican%CC%83a-de-ternera.jpg"));
        pinchos.items.add(new Item(UUID.randomUUID().toString(), "Quesadilla de Tortilla de Trigo con Pollo Jalapeño Queso Emmental y Salsa de Tomate Verde", null, 3.70F, "EUR", "taco", "http://www.lateral.com/wp-content/uploads/2015/05/Quesadilla-de-Tortilla-de-Trigo-con-Pollo-Jalapen%CC%83o-Queso-Emmental-y-Salsa-de-Tomate-Verde.jpg"));
        pinchos.items.add(new Item(UUID.randomUUID().toString(), "Ravioli de espinacas con salsa de queso de cabra pasas y piñones", null, 3.90F, "EUR", null, "http://www.lateral.com/wp-content/uploads/2015/05/Ravioli-de-espinacas-con-salsa-de-queso-de-cabra-pasas-y-pin%CC%83ones.jpg.jpg"));
        pinchos.items.add(new Item(UUID.randomUUID().toString(), "Capón relleno con salteado de ñoquis", null, 3.90F, "EUR", null, "http://www.lateral.com/wp-content/uploads/2016/05/Capon-relleno.jpg"));
        pinchos.items.add(new Item(UUID.randomUUID().toString(), "Tagliatelle salteado con verduras y pesto de tomates secos", null, 3.50F, "EUR", null, "http://www.lateral.com/wp-content/uploads/2016/05/Tagliatelle-salteado-con-verdura-3.jpg"));
        pinchos.items.add(new Item(UUID.randomUUID().toString(), "Arroz meloso con gambas al ajillo", null, 3.00F, "EUR", null, "http://www.lateral.com/wp-content/uploads/2016/05/Arroz-meloso-con-gambas-al-ajillo-1.jpg"));
        pinchos.items.add(new Item(UUID.randomUUID().toString(), "Bao-bun relleno de panceta asada en su jugo y salsa Hoisin", null, 4.20F, "EUR", "bao_bun", "http://www.lateral.com/wp-content/uploads/2016/05/SUGERENCIA-ARTURO-SORIA-bao-bun-relleno-de-panceta.jpg"));
        pinchos.items.add(new Item(UUID.randomUUID().toString(), "Lasaña de verduras", null, 4.00F, "EUR", "lasagna", "http://www.lateral.com/wp-content/uploads/2016/05/Lasan%CC%83a-de-verduras-NUEVO.jpg"));
        pinchos.items.add(new Item(UUID.randomUUID().toString(), "Mollete de romero", null, 3.80F, "EUR", "burger", "http://www.lateral.com/wp-content/uploads/2016/05/Mollete-de-romero.jpg"));
        pinchos.items.add(new Item(UUID.randomUUID().toString(), "Cerviche de merluza", null, 5.00F, "EUR", null, "http://www.lateral.com/wp-content/uploads/2016/05/Cerviche-de-merluza-1.jpg"));
        pinchos.items.add(new Item(UUID.randomUUID().toString(), "Albóndigas de merluza", null, 4.20F, "EUR", "meatballs", "http://www.lateral.com/wp-content/uploads/2016/05/Racio%CC%81n-de-albondigas-de-merluza-NUEVO.jpg"));
        pinchos.items.add(new Item(UUID.randomUUID().toString(), "Hamburguesa de buey", null, 4.00F, "EUR", "hamburger", "http://www.lateral.com/wp-content/uploads/2016/05/Hamburguesa-de-buey-e1464178621459.jpg"));
        menu.sections.add(pinchos);

        MenuSection miscellaneous = new MenuSection();
        miscellaneous.title = "Varios";
        miscellaneous.items.add(new Item(UUID.randomUUID().toString(), "Caldo s/t", null, 6.70F, "EUR", null, "http://www.lateral.com/wp-content/uploads/2015/05/Ensalada-de-guacamole-mozzarella-tomatitos-con-pesto-y-totopos-de-mai%CC%81z.jpg"));
        menu.sections.add(miscellaneous);

        MenuSection salads = new MenuSection();
        salads.title = "Ensaladas";
        salads.items.add(new Item(UUID.randomUUID().toString(), "Ensalada de guacamole mozzarella tomatitos con pesto y totopos de maíz", null, 6.70F, "EUR", null, "http://www.lateral.com/wp-content/uploads/2015/05/Ensalada-de-guacamole-mozzarella-tomatitos-con-pesto-y-totopos-de-mai%CC%81z.jpg"));
        salads.items.add(new Item(UUID.randomUUID().toString(), "Ensalada de pollo a la plancha, brotes verdes, cebolla tierna, api, manzana, queso emmental y aliño de mostaza", null, 6.70F, "EUR", null, null));
        salads.items.add(new Item(UUID.randomUUID().toString(), "Ensalada de queso de queso de cabra a la plancha, tomates, pipas de calabaza y vinagreta de tomates secos, miel Y Jerez", null, 6.70F, "EUR", null, "http://www.lateral.com/wp-content/uploads/2015/05/Ensalada-de-guacamole-mozzarella-tomatitos-con-pesto-y-totopos-de-mai%CC%81z.jpg"));
        menu.sections.add(salads);

        restaurant.menu = menu;

        return restaurant;

    }


}

