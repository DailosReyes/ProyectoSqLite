package es.dailos.proyectosqlite.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import es.dailos.proyectosqlite.R;
import es.dailos.proyectosqlite.adapter.RecyclerViewAdapter;
import es.dailos.proyectosqlite.dao.AnimalDAO;
import es.dailos.proyectosqlite.dao.RefugioDAO;
import es.dailos.proyectosqlite.dao.UsuarioDAO;
import es.dailos.proyectosqlite.databinding.ActivityAnimalBinding;
import es.dailos.proyectosqlite.entity.Animal;
import es.dailos.proyectosqlite.entity.Refugio;
import es.dailos.proyectosqlite.entity.Usuario;

public class AnimalActivity extends AppCompatActivity {

    private ActivityAnimalBinding binding;
    private AnimalDAO animalDAO;
    private RefugioDAO refugioDAO;
    private UsuarioDAO usuarioDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityAnimalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        animalDAO = new AnimalDAO(this);
        refugioDAO = new RefugioDAO(this);
        usuarioDAO = new UsuarioDAO(this);

        binding.toolbarLayout.tvToolbarTitle.setText("Animales");
        binding.toolbarLayout.btnBack.setOnClickListener(v -> finish());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        binding.btnInsertar.setOnClickListener(v -> mostrarFormularioInsertar());
        binding.btnEliminar.setOnClickListener(v -> mostrarPantallaEliminar());

        cargarListaAnimales();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarListaAnimales();
    }

    private void cargarListaAnimales() {
        List<Animal> animales = animalDAO.obtenerTodos();
        RecyclerViewAdapter<Animal> adapter = new RecyclerViewAdapter<>(
                animales,
                R.drawable.ic_animal,
                new RecyclerViewAdapter.TextBinder<Animal>() {
                    @Override
                    public String getTitle(Animal a) {
                        return a.getNombre();
                    }

                    @Override
                    public String getSubtitle(Animal a) {
                        String estado = a.getUsuarioId() > 0 ? " | 🏠 Adoptado" : " | En adopción";
                        return "Raza: " + (a.getRaza() != null ? a.getRaza() : "") + estado;
                    }
                },
                new RecyclerViewAdapter.OnItemClickListener<Animal>() {
                    @Override
                    public void onEdit(Animal item) {
                        mostrarFormularioEditar(item);
                    }

                    @Override
                    public void onInfo(Animal item) {
                        mostrarDetallesAnimal(item);
                    }
                }
        );
        binding.recyclerView.setAdapter(adapter);
    }

    private void mostrarFormularioInsertar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Insertar Animal");

        ScrollView scrollView = new ScrollView(this);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(40, 20, 40, 20);
        scrollView.addView(container);

        EditText etNombre = new EditText(this);
        etNombre.setHint("Nombre del animal");
        container.addView(etNombre);

        EditText etRaza = new EditText(this);
        etRaza.setHint("Raza");
        container.addView(etRaza);

        TextView tvSexo = new TextView(this);
        tvSexo.setText("\nSexo:");
        tvSexo.setTextSize(14.0f);
        container.addView(tvSexo);

        Spinner spSexo = new Spinner(this);
        String[] sexos = {"MACHO", "HEMBRA"};
        ArrayAdapter<String> adapterSexo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sexos);
        adapterSexo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSexo.setAdapter(adapterSexo);
        container.addView(spSexo);

        EditText etEdad = new EditText(this);
        etEdad.setHint("Edad (años)");
        etEdad.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        container.addView(etEdad);

        List<Refugio> refugios = refugioDAO.obtenerTodos();
        Spinner spRefugio = new Spinner(this);
        if (!refugios.isEmpty()) {
            TextView tvRef = new TextView(this);
            tvRef.setText("\nSelecciona un Refugio:");
            tvRef.setTextSize(14.0f);
            container.addView(tvRef);

            ArrayAdapter<Refugio> adapterRefugio = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, refugios);
            adapterRefugio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spRefugio.setAdapter(adapterRefugio);
            container.addView(spRefugio);
        }

        builder.setView(scrollView);
        builder.setPositiveButton("Guardar", null);
        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nom = etNombre.getText().toString().trim();
            String raza = etRaza.getText().toString().trim();
            String sexo = (String) spSexo.getSelectedItem();
            String edadStr = etEdad.getText().toString().trim();

            if (nom.isEmpty() || raza.isEmpty() || edadStr.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios (Nombre, Raza y Edad)", Toast.LENGTH_SHORT).show();
                return;
            }

            int edad;
            try {
                edad = Integer.parseInt(edadStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "La edad debe ser un número válido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (edad < 0 || edad > 100) {
                Toast.makeText(this, "La edad debe estar entre 0 y 100 años", Toast.LENGTH_SHORT).show();
                return;
            }

            if (refugios.isEmpty() || spRefugio.getSelectedItem() == null) {
                Toast.makeText(this, "Debe registrar y seleccionar al menos un refugio", Toast.LENGTH_SHORT).show();
                return;
            }

            Refugio rSel = (Refugio) spRefugio.getSelectedItem();
            int refugioId = rSel.getIdRefugio();

            long res = animalDAO.insertar(new Animal(nom, edad, raza, sexo, 0, refugioId));
            if (res > 0) {
                cargarListaAnimales();
                Toast.makeText(this, "Animal insertado con éxito", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Error al insertar el animal en la base de datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarPantallaEliminar() {
        List<Animal> animales = animalDAO.obtenerTodos();
        if (animales.isEmpty()) {
            Toast.makeText(this, "No hay animales para eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar Animal");

        ScrollView scrollView = new ScrollView(this);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(40, 20, 40, 20);
        scrollView.addView(container);

        TextView tvMsg = new TextView(this);
        tvMsg.setText("Selecciona el animal a eliminar:");
        tvMsg.setTextSize(16.0f);
        container.addView(tvMsg);

        Spinner spinner = new Spinner(this);
        ArrayAdapter<Animal> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, animales);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        container.addView(spinner);

        builder.setView(scrollView);
        builder.setPositiveButton("Eliminar Animal", (dialog, which) -> {
            Animal aSel = (Animal) spinner.getSelectedItem();
            if (aSel != null) {
                animalDAO.eliminar(aSel.getIdAnimales());
                cargarListaAnimales();
                Toast.makeText(this, "Animal eliminado", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void mostrarDetallesAnimal(Animal animal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Detalles del Animal");

        ScrollView scrollView = new ScrollView(this);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(40, 20, 40, 20);
        scrollView.addView(container);

        Refugio refugio = animal.getRefugioId() > 0 ? refugioDAO.obtenerPorId(animal.getRefugioId()) : null;
        String nombreRefugio = refugio != null ? refugio.getNombre() : "Ninguno";

        Usuario usuario = animal.getUsuarioId() > 0 ? usuarioDAO.obtenerPorId(animal.getUsuarioId()) : null;
        String adoptadoPor = usuario != null
                ? usuario.getNombre() + " " + (usuario.getApellidos() != null ? usuario.getApellidos() : "")
                : "Sin adoptar (Disponible)";

        TextView tvInfo = new TextView(this);
        tvInfo.setText("Nombre: " + animal.getNombre() + "\n"
                + "Raza: " + animal.getRaza() + "\n"
                + "Sexo: " + animal.getSexo() + "\n"
                + "Edad: " + animal.getEdad() + " años\n"
                + "Refugio: " + nombreRefugio + "\n"
                + "Adoptado por: " + adoptadoPor);
        tvInfo.setTextSize(16.0f);
        container.addView(tvInfo);

        builder.setView(scrollView);
        builder.setNeutralButton("Modificar Datos", (dialog, which) -> mostrarFormularioEditar(animal));
        builder.setNegativeButton("Cerrar", null);
        builder.show();
    }

    private void mostrarFormularioEditar(Animal animal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Animal");

        ScrollView scrollView = new ScrollView(this);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(40, 20, 40, 20);
        scrollView.addView(container);

        TextView tvNom = new TextView(this);
        tvNom.setText("Nombre:");
        tvNom.setTextSize(14.0f);
        container.addView(tvNom);

        EditText etNombre = new EditText(this);
        etNombre.setText(animal.getNombre());
        container.addView(etNombre);

        TextView tvRaza = new TextView(this);
        tvRaza.setText("\nRaza:");
        tvRaza.setTextSize(14.0f);
        container.addView(tvRaza);

        EditText etRaza = new EditText(this);
        etRaza.setText(animal.getRaza());
        container.addView(etRaza);

        TextView tvSexo = new TextView(this);
        tvSexo.setText("\nSexo:");
        tvSexo.setTextSize(14.0f);
        container.addView(tvSexo);

        Spinner spSexo = new Spinner(this);
        String[] sexos = {"MACHO", "HEMBRA"};
        ArrayAdapter<String> adapterSexo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sexos);
        adapterSexo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSexo.setAdapter(adapterSexo);
        if ("HEMBRA".equalsIgnoreCase(animal.getSexo())) {
            spSexo.setSelection(1);
        } else {
            spSexo.setSelection(0);
        }
        container.addView(spSexo);

        TextView tvEdad = new TextView(this);
        tvEdad.setText("\nEdad (años):");
        tvEdad.setTextSize(14.0f);
        container.addView(tvEdad);

        EditText etEdad = new EditText(this);
        etEdad.setText(String.valueOf(animal.getEdad()));
        etEdad.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        container.addView(etEdad);

        List<Refugio> refugios = refugioDAO.obtenerTodos();
        Spinner spRefugio = new Spinner(this);
        if (!refugios.isEmpty()) {
            TextView tvRef = new TextView(this);
            tvRef.setText("\nRefugio:");
            tvRef.setTextSize(14.0f);
            container.addView(tvRef);

            ArrayAdapter<Refugio> adapterRefugio = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, refugios);
            adapterRefugio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spRefugio.setAdapter(adapterRefugio);
            for (int i = 0; i < refugios.size(); i++) {
                if (refugios.get(i).getIdRefugio() == animal.getRefugioId()) {
                    spRefugio.setSelection(i);
                    break;
                }
            }
            container.addView(spRefugio);
        }

        List<Usuario> usuarios = usuarioDAO.obtenerTodos();
        Spinner spUsuario = new Spinner(this);
        TextView tvUsr = new TextView(this);
        tvUsr.setText("\nAdoptante:");
        tvUsr.setTextSize(14.0f);
        container.addView(tvUsr);

        List<String> opcionesUsuario = new ArrayList<>();
        opcionesUsuario.add("Sin adoptar (Disponible)");
        int posSel = 0;
        for (int i = 0; i < usuarios.size(); i++) {
            Usuario u = usuarios.get(i);
            opcionesUsuario.add(u.getNombre() + " " + (u.getApellidos() != null ? u.getApellidos() : ""));
            if (u.getIdusuario() == animal.getUsuarioId()) {
                posSel = i + 1;
            }
        }
        ArrayAdapter<String> adapterUsuario = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesUsuario);
        adapterUsuario.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUsuario.setAdapter(adapterUsuario);
        spUsuario.setSelection(posSel);
        container.addView(spUsuario);

        builder.setView(scrollView);
        builder.setPositiveButton("Guardar Cambios", null);
        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nom = etNombre.getText().toString().trim();
            String raza = etRaza.getText().toString().trim();
            String eStr = etEdad.getText().toString().trim();

            if (nom.isEmpty() || raza.isEmpty() || eStr.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios (Nombre, Raza y Edad)", Toast.LENGTH_SHORT).show();
                return;
            }

            int edad;
            try {
                edad = Integer.parseInt(eStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "La edad debe ser un número válido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (edad < 0 || edad > 100) {
                Toast.makeText(this, "La edad debe estar entre 0 y 100 años", Toast.LENGTH_SHORT).show();
                return;
            }

            animal.setNombre(nom);
            animal.setRaza(raza);
            animal.setSexo((String) spSexo.getSelectedItem());
            animal.setEdad(edad);

            if (!refugios.isEmpty() && spRefugio.getSelectedItem() != null) {
                Refugio rSel = (Refugio) spRefugio.getSelectedItem();
                animal.setRefugioId(rSel.getIdRefugio());
            }

            if (spUsuario.getSelectedItemPosition() == 0) {
                animal.setUsuarioId(0);
            } else {
                Usuario uSel = usuarios.get(spUsuario.getSelectedItemPosition() - 1);
                animal.setUsuarioId(uSel.getIdusuario());
            }

            animalDAO.actualizar(animal);
            cargarListaAnimales();
            Toast.makeText(this, "Animal actualizado", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }
}