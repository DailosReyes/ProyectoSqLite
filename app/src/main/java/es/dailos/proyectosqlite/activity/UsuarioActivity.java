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

import java.util.List;

import es.dailos.proyectosqlite.R;
import es.dailos.proyectosqlite.adapter.RecyclerViewAdapter;
import es.dailos.proyectosqlite.dao.AnimalDAO;
import es.dailos.proyectosqlite.dao.UsuarioDAO;
import es.dailos.proyectosqlite.databinding.ActivityUsuarioBinding;
import es.dailos.proyectosqlite.entity.Animal;
import es.dailos.proyectosqlite.entity.Usuario;

public class UsuarioActivity extends AppCompatActivity {

    private ActivityUsuarioBinding binding;
    private UsuarioDAO usuarioDAO;
    private AnimalDAO animalDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityUsuarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        usuarioDAO = new UsuarioDAO(this);
        animalDAO = new AnimalDAO(this);

        binding.toolbarLayout.tvToolbarTitle.setText("Usuarios");
        binding.toolbarLayout.btnBack.setOnClickListener(v -> finish());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        binding.btnInsertar.setOnClickListener(v -> mostrarFormularioInsertar());
        binding.btnEliminar.setOnClickListener(v -> mostrarPantallaEliminar());

        cargarListaUsuarios();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarListaUsuarios();
    }

    private void cargarListaUsuarios() {
        List<Usuario> usuarios = usuarioDAO.obtenerTodos();
        RecyclerViewAdapter<Usuario> adapter = new RecyclerViewAdapter<>(
                usuarios,
                R.drawable.ic_usuario,
                new RecyclerViewAdapter.TextBinder<Usuario>() {
                    @Override
                    public String getTitle(Usuario u) {
                        return u.getNombre() + " " + (u.getApellidos() != null ? u.getApellidos() : "");
                    }

                    @Override
                    public String getSubtitle(Usuario u) {
                        return (u.getDni() != null ? u.getDni() : "");
                    }
                },
                new RecyclerViewAdapter.OnItemClickListener<Usuario>() {
                    @Override
                    public void onEdit(Usuario item) {
                        mostrarFormularioEditar(item);
                    }

                    @Override
                    public void onInfo(Usuario item) {
                        mostrarDetallesUsuario(item);
                    }
                }
        );
        binding.recyclerView.setAdapter(adapter);
    }

    private void mostrarFormularioInsertar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Insertar Usuario");

        ScrollView scrollView = new ScrollView(this);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(40, 20, 40, 20);
        scrollView.addView(container);

        EditText etNombre = new EditText(this);
        etNombre.setHint("Nombre");
        container.addView(etNombre);

        EditText etApellidos = new EditText(this);
        etApellidos.setHint("Apellidos");
        container.addView(etApellidos);

        EditText etDni = new EditText(this);
        etDni.setHint("DNI");
        container.addView(etDni);

        EditText etTel = new EditText(this);
        etTel.setHint("Teléfono");
        etTel.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        container.addView(etTel);

        builder.setView(scrollView);
        builder.setPositiveButton("Guardar", null);
        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nom = etNombre.getText().toString().trim();
            String ape = etApellidos.getText().toString().trim();
            String dni = etDni.getText().toString().trim();
            String tel = etTel.getText().toString().trim();

            if (nom.isEmpty() || ape.isEmpty() || dni.isEmpty() || tel.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios (Nombre, Apellidos, DNI y Teléfono)", Toast.LENGTH_SHORT).show();
                return;
            }

            usuarioDAO.insertar(new Usuario(nom, ape, dni, tel));
            cargarListaUsuarios();
            Toast.makeText(this, "Usuario insertado", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

    private void mostrarPantallaEliminar() {
        List<Usuario> usuarios = usuarioDAO.obtenerTodos();
        if (usuarios.isEmpty()) {
            Toast.makeText(this, "No hay usuarios para eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar Usuario");

        ScrollView scrollView = new ScrollView(this);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(40, 20, 40, 20);
        scrollView.addView(container);

        TextView tvMsg = new TextView(this);
        tvMsg.setText("Selecciona el usuario a eliminar:");
        tvMsg.setTextSize(16.0f);
        container.addView(tvMsg);

        Spinner spinner = new Spinner(this);
        ArrayAdapter<Usuario> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, usuarios);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        container.addView(spinner);

        builder.setView(scrollView);
        builder.setPositiveButton("Eliminar Usuario", (dialog, which) -> {
            Usuario uSel = (Usuario) spinner.getSelectedItem();
            if (uSel != null) {
                usuarioDAO.eliminar(uSel.getIdusuario());
                cargarListaUsuarios();
                Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void mostrarDetallesUsuario(Usuario usuario) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Detalles de Usuario");

        ScrollView scrollView = new ScrollView(this);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(40, 20, 40, 20);
        scrollView.addView(container);

        List<Animal> misAnimales = animalDAO.obtenerPorUsuario(usuario.getIdusuario());
        StringBuilder sbAnimales = new StringBuilder();
        if (misAnimales.isEmpty()) {
            sbAnimales.append("Ninguno");
        } else {
            for (int i = 0; i < misAnimales.size(); i++) {
                if (i > 0) sbAnimales.append(", ");
                sbAnimales.append(misAnimales.get(i).getNombre());
            }
        }

        TextView tvInfo = new TextView(this);
        tvInfo.setText("Nombre: " + usuario.getNombre() + " " + (usuario.getApellidos() != null ? usuario.getApellidos() : "") + "\n"
                + "DNI: " + (usuario.getDni() != null ? usuario.getDni() : "") + "\n"
                + "Teléfono: " + (usuario.getTelefono() != null ? usuario.getTelefono() : "") + "\n"
                + "Animales adoptados: " + sbAnimales);
        tvInfo.setTextSize(16.0f);
        container.addView(tvInfo);

        List<Animal> animalesDisponibles = animalDAO.obtenerNoAdoptados();

        TextView tvAdop = new TextView(this);
        tvAdop.setText("\nAdoptar un Animal:");
        tvAdop.setTypeface(null, android.graphics.Typeface.BOLD);
        tvAdop.setTextSize(16.0f);
        container.addView(tvAdop);

        if (animalesDisponibles.isEmpty()) {
            TextView tvNoAnimales = new TextView(this);
            tvNoAnimales.setText("No hay animales disponibles para adoptar.");
            tvNoAnimales.setTextSize(14.0f);
            container.addView(tvNoAnimales);

            builder.setView(scrollView);
        } else {
            Spinner spAnimales = new Spinner(this);
            ArrayAdapter<Animal> adapterA = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, animalesDisponibles);
            adapterA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spAnimales.setAdapter(adapterA);
            container.addView(spAnimales);

            builder.setView(scrollView);
            builder.setPositiveButton("Adoptar", (dialog, which) -> {
                Animal aSel = (Animal) spAnimales.getSelectedItem();
                if (aSel != null) {
                    aSel.setUsuarioId(usuario.getIdusuario());
                    animalDAO.actualizar(aSel);
                    Toast.makeText(this, "Animal adoptado con éxito", Toast.LENGTH_SHORT).show();
                }
            });
        }

        builder.setNeutralButton("Modificar Datos", (dialog, which) -> mostrarFormularioEditar(usuario));
        builder.setNegativeButton("Cerrar", null);
        builder.show();
    }

    private void mostrarFormularioEditar(Usuario usuario) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Usuario");

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
        etNombre.setText(usuario.getNombre());
        container.addView(etNombre);

        TextView tvApe = new TextView(this);
        tvApe.setText("\nApellidos:");
        tvApe.setTextSize(14.0f);
        container.addView(tvApe);

        EditText etApellidos = new EditText(this);
        etApellidos.setText(usuario.getApellidos());
        container.addView(etApellidos);

        TextView tvDni = new TextView(this);
        tvDni.setText("\nDNI:");
        tvDni.setTextSize(14.0f);
        container.addView(tvDni);

        EditText etDni = new EditText(this);
        etDni.setText(usuario.getDni());
        container.addView(etDni);

        TextView tvTel = new TextView(this);
        tvTel.setText("\nTeléfono:");
        tvTel.setTextSize(14.0f);
        container.addView(tvTel);

        EditText etTel = new EditText(this);
        etTel.setText(usuario.getTelefono());
        etTel.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        container.addView(etTel);

        builder.setView(scrollView);
        builder.setPositiveButton("Guardar Cambios", null);
        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nom = etNombre.getText().toString().trim();
            String ape = etApellidos.getText().toString().trim();
            String dni = etDni.getText().toString().trim();
            String tel = etTel.getText().toString().trim();

            if (nom.isEmpty() || ape.isEmpty() || dni.isEmpty() || tel.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios (Nombre, Apellidos, DNI y Teléfono)", Toast.LENGTH_SHORT).show();
                return;
            }

            usuario.setNombre(nom);
            usuario.setApellidos(ape);
            usuario.setDni(dni);
            usuario.setTelefono(tel);

            usuarioDAO.actualizar(usuario);
            cargarListaUsuarios();
            Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }
}