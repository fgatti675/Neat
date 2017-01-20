package com.neat.backend;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by f.gatti.gomez on 02/01/2017.
 */

public class Admin {

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setServiceAccount(new FileInputStream("/Users/f.gatti.gomez/neat-eb7c6-firebase-adminsdk-edcz4-8078dde852.json"))
                .setDatabaseUrl("https://neat-eb7c6.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);

        saveLateral();

    }

    public static void saveLateral() throws InterruptedException {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        DatabaseReference lateralRef = database.child("restaurants").child("lateral");

        lateralRef.child("title").setValue("Lateral");
        lateralRef.child("subtitle").setValue("Vive la experiencia");
        lateralRef.child("headerUrl").setValue("http://www.lateral.com/wp-content/uploads/2015/04/cabecera-carta.jpg");
//        lateralRef.child("headerUrl").setValue("http://www.lateral.com/wp-content/uploads/2015/05/LateralArturoSoria-portada.jpg");

        DatabaseReference menuRef = lateralRef.child("menu");
        menuRef.child("currency").setValue("EUR");

        DatabaseReference itemsRef = menuRef.child("items");

        DatabaseReference drinksRef = addSection(menuRef, "small", "¿Tenéis sed?", "Bebidas", "Nuestros clientes suelen pedir estas bebidas, pero tenemos muchas más");

        DatabaseReference beersRef = addSection(drinksRef, null, null, "Cervezas", null);
        addItem("Copa de Mahou", "31cl.", true, 2.70D, "EUR", null, "glass_small", itemsRef, beersRef);
        addItem("Doble de Mahou", "50cl.", false, 4.50D, "EUR", null, "brandy_glass", itemsRef, beersRef);
        addItem("Copa Laiker", "Sin alcohol", false, 2.70D, "EUR", null, "glass_small_alt", itemsRef, beersRef);
        addItem("1/3 Mahou negra", null, false, 3.35D, "EUR", null, "beer_bottle", itemsRef, beersRef);
        addItem("1/3 Mahou mixta", null, true, 2.90D, "EUR", null, "beer_bottle", itemsRef, beersRef);
        addItem("1/3 Mahou light", null, false, 2.90D, "EUR", null, "beer_bottle", itemsRef, beersRef);
        addItem("1/3 Carlsberg", null, false, 2.90D, "EUR", null, "beer_bottle", itemsRef, beersRef);
        addItem("1/3 Alhambra", null, false, 3.00D, "EUR", null, "beer_bottle", itemsRef, beersRef);

        DatabaseReference winesRef = addSection(drinksRef, null, null, "Vinos", null);
        DatabaseReference riojasRef = addSection(winesRef, null, null, "Riojas", null);
        addItem("Casa Viña Real Reserva", null, true, 2.70D, "EUR", null, "wine_glass", itemsRef, riojasRef);
        addItem("Casa Viña Real Reserva", "50cl.", false, 9.95D, "EUR", null, "wine_glasses", itemsRef, riojasRef);
        addItem("Casa Viña Real Reserva", "75cl.", false, 13.45D, "EUR", null, "wine_glasses", itemsRef, riojasRef);
        addItem("Contino Gran Reserva", "75cl.", false, 28.0D, "EUR", null, "wine_glasses", itemsRef, riojasRef);
        addItem("Copa tinto de verano", null, true, 2.85D, "EUR", null, "glass_small_alt", itemsRef, riojasRef);
        DatabaseReference riberasRef = addSection(winesRef, null, null, "Ribera del Duero", null);
        addItem("Viña Solorca Reserva", null, false, 3.35D, "EUR", null, "wine_glass", itemsRef, riberasRef);
        DatabaseReference ruedasRef = addSection(winesRef, null, null, "Rueda", null);
        addItem("Palacio de Bornos", null, true, 2.60D, "EUR", null, "wine_glass", itemsRef, ruedasRef);
        addItem("Palacio de Bornos", null, false, 12.60D, "EUR", null, "wine_glasses", itemsRef, ruedasRef);

        DatabaseReference featuredRef = addSection(menuRef, "featured", "Hoy te recomendamos", "Recomendados", "Disfruta con lo que hemos preparado hoy especial para ti");

        DatabaseReference pinchosRef = addSection(menuRef, "list", null, "Pinchos", null);
        
        addItem("Arroz negro con chipirones", null, false, 3.60D, "EUR", "http://www.lateral.com/wp-content/uploads/2015/05/Arroz-negro-con-chipirones.jpeg", "octopus", itemsRef, featuredRef, pinchosRef);
        addItem("Magret de Pato con Chutney de Piña y Mango", null, false, 3.90D, "EUR", "http://www.lateral.com/wp-content/uploads/2015/05/Magret-de-Pato-con-Chutney-de-Pin%CC%83a-y-Mango.jpg", "duck", itemsRef, featuredRef, pinchosRef);
        addItem("Morcilla con patata paja con yema de huevo de corral", null, false, 4.00D, "EUR", "http://www.lateral.com/wp-content/uploads/2015/05/Morcilla-con-patata-paja-con-yema-de-huevo-de-corral.jpeg", "eggs", itemsRef, featuredRef, pinchosRef);

        addItem("Picaña de ternera", null, false, 4.20D, "EUR", "http://www.lateral.com/wp-content/uploads/2015/05/Pican%CC%83a-de-ternera.jpg", "steak", itemsRef, pinchosRef);
        addItem("Quesadilla de Tortilla de Trigo con Pollo Jalapeño Queso Emmental y Salsa de Tomate Verde", null, false, 3.70D, "EUR", "http://www.lateral.com/wp-content/uploads/2015/05/Quesadilla-de-Tortilla-de-Trigo-con-Pollo-Jalapen%CC%83o-Queso-Emmental-y-Salsa-de-Tomate-Verde.jpg", null, itemsRef, pinchosRef);
        addItem("Ravioli de espinacas con salsa de queso de cabra pasas y piñones", null, false, 3.90D, "EUR", "http://www.lateral.com/wp-content/uploads/2015/05/Ravioli-de-espinacas-con-salsa-de-queso-de-cabra-pasas-y-pin%CC%83ones.jpg.jpg", null, itemsRef, pinchosRef);
        addItem("Capón relleno con salteado de ñoquis", null, false, 3.90D, "EUR", "http://www.lateral.com/wp-content/uploads/2016/05/Capon-relleno.jpg", null, itemsRef, pinchosRef);
        addItem("Tagliatelle salteado con verduras y pesto de tomates secos", null, false, 3.50D, "EUR", "http://www.lateral.com/wp-content/uploads/2016/05/Tagliatelle-salteado-con-verdura-3.jpg", null, itemsRef, pinchosRef);
        addItem("Arroz meloso con gambas al ajillo", null, false, 3.00D, "EUR", "http://www.lateral.com/wp-content/uploads/2016/05/Arroz-meloso-con-gambas-al-ajillo-1.jpg", null, itemsRef, pinchosRef);
        addItem("Bao-bun relleno de panceta asada en su jugo y salsa Hoisin", null, false, 4.20D, "EUR", "http://www.lateral.com/wp-content/uploads/2016/05/SUGERENCIA-ARTURO-SORIA-bao-bun-relleno-de-panceta.jpg", "bao-bun", itemsRef, pinchosRef);
        addItem("Lasaña de verduras", null, false, 4.00D, "EUR", "http://www.lateral.com/wp-content/uploads/2016/05/Lasan%CC%83a-de-verduras-NUEVO.jpg", null, itemsRef, pinchosRef);
        addItem("Mollete de romero", null, false, 3.80D, "EUR", "http://www.lateral.com/wp-content/uploads/2016/05/Mollete-de-romero.jpg", "hamburger", itemsRef, pinchosRef);
        addItem("Cerviche de merluza", null, false, 5.00D, "EUR", "http://www.lateral.com/wp-content/uploads/2016/05/Cerviche-de-merluza-1.jpg", null, itemsRef, pinchosRef);
        addItem("Albóndigas de merluza", null, false, 4.20D, "EUR", "http://www.lateral.com/wp-content/uploads/2016/05/Racio%CC%81n-de-albondigas-de-merluza-NUEVO.jpg", null, itemsRef, pinchosRef);
        addItem("Hamburguesa de buey", null, false, 4.00D, "EUR", "http://www.lateral.com/wp-content/uploads/2016/05/Hamburguesa-de-buey-e1464178621459.jpg", "hamburger", itemsRef, pinchosRef);

        Thread.sleep(5000);

    }

    private static DatabaseReference addItem(String name, String description, boolean featured, double price, String currency, String imageUrl, String icon, DatabaseReference itemsRef, DatabaseReference... sectionRefs) {
        DatabaseReference reference = itemsRef.push();
        reference.child("name").setValue(name);
        reference.child("description").setValue(description);
        reference.child("featured").setValue(featured);
        reference.child("price").setValue(price);
        reference.child("currency").setValue(currency);
        reference.child("icon").setValue(icon);
        reference.child("imageUrl").setValue(imageUrl);
        for (DatabaseReference sectionRef : sectionRefs)
            sectionRef.child("items").child(reference.getKey()).setValue(true);
        return reference;
    }

    private static DatabaseReference addSection(DatabaseReference parentRef, String type, String headline, String title, String subtitle) {
        DatabaseReference reference = parentRef.child("sections").push();
        reference.child("type").setValue(type);
        reference.child("headline").setValue(headline);
        reference.child("title").setValue(title);
        reference.child("subtitle").setValue(subtitle);
        return reference;
    }


}
