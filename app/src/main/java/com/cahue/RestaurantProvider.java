package com.cahue;

import com.cahue.model.Item;
import com.cahue.model.Menu;
import com.cahue.model.MenuSection;
import com.cahue.model.Restaurant;

/**
 * Created by f.gatti.gomez on 10/10/16.
 */

public class RestaurantProvider {

    public static Restaurant getLateral() {

        Restaurant restaurant = new Restaurant();

        restaurant.name = "Lateral";
        restaurant.subtitle = "Vive la experiencia";

        Menu menu = new Menu();

        MenuSection drinks = new MenuSection();
        drinks.type = MenuSection.Type.TAGS;
        drinks.headline = "¿Tenéis sed?";
        drinks.title = "Bebidas";
        drinks.subtitle = "Nuestros clientes suelen pedir estas bebidas, pero tenemos muchas más";
        MenuSection beers = new MenuSection();
        beers.title = "Cervezas";
        beers.items.add(new Item("Copa de Mahou", "31cl.", true, 2.70F, "EUR", "glass_small", null));
        beers.items.add(new Item("Doble de Mahou", "50cl.", false, 4.50F, "EUR", "brandy_glass", null));
        beers.items.add(new Item("Copa Laiker", "Sin alcohol", false, 2.70F, "EUR", "glass_small_alt", null));
        beers.items.add(new Item("1/3 Mahou negra", null, true, 3.35F, "EUR", "beer_bottle", null));
        beers.items.add(new Item("1/3 Mahou mixta", null, true, 2.90F, "EUR", "beer_bottle", null));
        beers.items.add(new Item("1/3 Mahou light", null, true, 2.90F, "EUR", "beer_bottle", null));
        beers.items.add(new Item("1/3 Carlsberg", null, false, 2.90F, "EUR", "beer_bottle", null));
        beers.items.add(new Item("1/3 Alhambra", null, false, 3.00F, "EUR", "beer_bottle", null));
        drinks.subsections.add(beers);
        MenuSection vines = new MenuSection();
        vines.title = "Vinos";
        MenuSection riojas = new MenuSection();
        riojas.title = "Riojas";
        riojas.items.add(new Item("Casa Viña Real Reserva", null, true, 2.70F, "EUR", "wine_glass", null));
        riojas.items.add(new Item("Casa Viña Real Reserva", "50cl.", false, 9.95F, "EUR", "wine_glasses", null));
        riojas.items.add(new Item("Casa Viña Real Reserva", "75cl.", false, 13.45F, "EUR", "wine_glasses", null));
        riojas.items.add(new Item("Contino Gran Reserva", "75cl.", false, 28.0F, "EUR", "wine_glasses", null));
        riojas.items.add(new Item("Copa tinto de verano", null, false, 2.85F, "EUR", "glass_small_alt", null));
        vines.subsections.add(riojas);
        MenuSection riberas = new MenuSection();
        riberas.title = "Ribera del Duero";
        riberas.items.add(new Item("Viña Solorca Reserva", null, true, 3.35F, "EUR", "wine_glass", null));
        vines.subsections.add(riberas);
        MenuSection ruedas = new MenuSection();
        ruedas.title = "Rueda";
        ruedas.items.add(new Item("Palacio de Bornos", null, true, 2.60F, "EUR", "wine_glass", null));
        ruedas.items.add(new Item("Palacio de Bornos", null, false, 12.60F, "EUR", "wine_glasses", null));
        vines.subsections.add(ruedas);
        drinks.subsections.add(vines);
        menu.sections.add(drinks);


        MenuSection featured = new MenuSection();
        featured.headline = "Hoy te recomendamos";
        featured.subtitle = "Disfruta con lo que hemos preparado hoy especial para ti";
        featured.type = MenuSection.Type.FEATURED;
        Item p1 = new Item("Arroz negro con chipirones", null, true, 3.60F, "EUR", "octopus", "http://www.lateral.com/wp-content/uploads/2015/05/Arroz-negro-con-chipirones.jpeg");
        Item p2 = new Item("Magret de Pato con Chutney de Piña y Mango", null, true, 3.90F, "EUR", null, "http://www.lateral.com/wp-content/uploads/2015/05/Magret-de-Pato-con-Chutney-de-Pin%CC%83a-y-Mango.jpg");
        Item p3 = new Item("Morcilla con patata paja con yema de huevo de corral", null, true, 4.00F, "EUR", "eggs", "http://www.lateral.com/wp-content/uploads/2015/05/Morcilla-con-patata-paja-con-yema-de-huevo-de-corral.jpeg");
        featured.items.add(p1);
        featured.items.add(p2);
        featured.items.add(p3);
        menu.sections.add(featured);

        MenuSection pinchos = new MenuSection();
        pinchos.type = MenuSection.Type.LIST;
        pinchos.title = "Pinchos";
        pinchos.items.add(p1);
        pinchos.items.add(p2);
        pinchos.items.add(p3);
        pinchos.items.add(new Item("Picaña de ternera", null, true, 4.20F, "EUR", null, "http://www.lateral.com/wp-content/uploads/2015/05/Pican%CC%83a-de-ternera.jpg"));
        pinchos.items.add(new Item("Quesadilla de Tortilla de Trigo con Pollo Jalapeño Queso Emmental y Salsa de Tomate Verde", null, true, 3.70F, "EUR", null, "http://www.lateral.com/wp-content/uploads/2015/05/Quesadilla-de-Tortilla-de-Trigo-con-Pollo-Jalapen%CC%83o-Queso-Emmental-y-Salsa-de-Tomate-Verde.jpg"));
        pinchos.items.add(new Item("Ravioli de espinacas con salsa de queso de cabra pasas y piñones", null, true, 3.90F, "EUR", null, "http://www.lateral.com/wp-content/uploads/2015/05/Ravioli-de-espinacas-con-salsa-de-queso-de-cabra-pasas-y-pin%CC%83ones.jpg.jpg"));
        pinchos.items.add(new Item("Capón relleno con salteado de ñoquis", null, true, 3.90F, "EUR", null, "http://www.lateral.com/wp-content/uploads/2016/05/Capon-relleno.jpg"));
        pinchos.items.add(new Item("Tagliatelle salteado con verduras y pesto de tomates secos", null, true, 3.50F, "EUR", null, "http://www.lateral.com/wp-content/uploads/2016/05/Tagliatelle-salteado-con-verdura-3.jpg"));
        pinchos.items.add(new Item("Arroz meloso con gambas al ajillo", null, true, 3.00F, "EUR", null, "http://www.lateral.com/wp-content/uploads/2016/05/Arroz-meloso-con-gambas-al-ajillo-1.jpg"));
        pinchos.items.add(new Item("Bao-bun relleno de panceta asada en su jugo y salsa Hoisin", null, true, 4.20F, "EUR", null, "http://www.lateral.com/wp-content/uploads/2016/05/SUGERENCIA-ARTURO-SORIA-bao-bun-relleno-de-panceta.jpg"));
        pinchos.items.add(new Item("Lasaña de verduras", null, true, 4.00F, "EUR", null, "http://www.lateral.com/wp-content/uploads/2016/05/Lasan%CC%83a-de-verduras-NUEVO.jpg"));
        pinchos.items.add(new Item("Mollete de romero", null, true, 3.80F, "EUR", null, "http://www.lateral.com/wp-content/uploads/2016/05/Mollete-de-romero.jpg"));
        pinchos.items.add(new Item("Cerviche de merluza", null, true, 5.00F, "EUR", null, "http://www.lateral.com/wp-content/uploads/2016/05/Cerviche-de-merluza-1.jpg"));
        pinchos.items.add(new Item("Albóndigas de merluza", null, true, 4.20F, "EUR", null, "http://www.lateral.com/wp-content/uploads/2016/05/Racio%CC%81n-de-albondigas-de-merluza-NUEVO.jpg"));
        pinchos.items.add(new Item("Hamburguesa de buey", null, true, 4.00F, "EUR", null, "http://www.lateral.com/wp-content/uploads/2016/05/Hamburguesa-de-buey-e1464178621459.jpg"));
        menu.sections.add(pinchos);

        MenuSection miscellaneous = new MenuSection();
        miscellaneous.title = "Varios";
        miscellaneous.items.add(new Item("Caldo s/t", null, true, 6.70F, "EUR", null, "http://www.lateral.com/wp-content/uploads/2015/05/Ensalada-de-guacamole-mozzarella-tomatitos-con-pesto-y-totopos-de-mai%CC%81z.jpg"));
        menu.sections.add(miscellaneous);

        MenuSection salads = new MenuSection();
        salads.title = "Ensaladas";
        salads.items.add(new Item("Ensalada de guacamole mozzarella tomatitos con pesto y totopos de maíz", null, true, 6.70F, "EUR", null, "http://www.lateral.com/wp-content/uploads/2015/05/Ensalada-de-guacamole-mozzarella-tomatitos-con-pesto-y-totopos-de-mai%CC%81z.jpg"));
        salads.items.add(new Item("Ensalada de pollo a la plancha, brotes verdes, cebolla tierna, api, manzana, queso emmental y aliño de mostaza", null, true, 6.70F, "EUR", null, null));
        salads.items.add(new Item("Ensalada de queso de queso de cabra a la plancha, tomates, pipas de calabaza y vinagreta de tomates secos, miel Y Jerez", null, true, 6.70F, "EUR", null, "http://www.lateral.com/wp-content/uploads/2015/05/Ensalada-de-guacamole-mozzarella-tomatitos-con-pesto-y-totopos-de-mai%CC%81z.jpg"));
        menu.sections.add(salads);

        menu.sections.add(beers);

        restaurant.menu = menu;

        return restaurant;

    }
}
