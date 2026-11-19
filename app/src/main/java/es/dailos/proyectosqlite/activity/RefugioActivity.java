package es.dailos.proyectosqlite.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import es.dailos.proyectosqlite.dao.RefugioDAO;
import es.dailos.proyectosqlite.databinding.ActivityRefugioBinding;
import es.dailos.proyectosqlite.entity.Refugio;

public class RefugioActivity extends AppCompatActivity {

    private ActivityRefugioBinding binding;
    private RefugioDAO refugioDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityRefugioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        refugioDAO = new RefugioDAO(this);

        binding.toolbarLayout.tvToolbarTitle.setText("Refugios");
        binding.toolbarLayout.btnBack.setOnClickListener(v -> finish());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        binding.btnInsertar.setOnClickListener(v -> mostrarFormularioInsertar());
        binding.btnEliminar.setOnClickListener(v -> mostrarPantallaEliminar());

        cargarListaRefugios();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarListaRefugios();
    }

    private void cargarListaRefugios() {
        List<Refugio> refugios = refugioDAO.obtenerTodos();
        RecyclerViewAdapter<Refugio> adapter = new RecyclerViewAdapter<>(
                refugios,
                R.drawable.ic_refugio,
                new RecyclerViewAdapter.TextBinder<Refugio>() {
                    @Override
                    public String getTitle(Refugio r) {
                        return r.getNombre();
                    }

                    @Override
                    public String getSubtitle(Refugio r) {
                        return (r.getDireccion() != null ? r.getDireccion() : "");
                    }
                },
                new RecyclerViewAdapter.OnItemClickListener<Refugio>() {
                    @Override
                    public void onEdit(Refugio item) {
                        mostrarFormularioEditar(item);
                    }

                    @Override
                    public void onInfo(Refugio item) {
                        mostrarDetallesRefugio(item);
                    }
                }
        );
        binding.recyclerView.setAdapter(adapter);
    }

    private void mostrarFormularioInsertar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Insertar Refugio");

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(40, 20, 40, 20);

        EditText etNombre = new EditText(this);
        etNombre.setHint("Nombre del Refugio");
        container.addView(etNombre);

        EditText etDireccion = new EditText(this);
        etDireccion.setHint("Dirección");
        container.addView(etDireccion);

        EditText etTel = new EditText(this);
        etTel.setHint("Teléfono");
        etTel.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        container.addView(etTel);

        builder.setView(container);
        builder.setPositiveButton("Guardar", null);
        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nom = etNombre.getText().toString().trim();
            String dir = etDireccion.getText().toString().trim();
            String tel = etTel.getText().toString().trim();

            if (nom.isEmpty() || dir.isEmpty() || tel.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios (Nombre, Dirección y Teléfono)", Toast.LENGTH_SHORT).show();
                return;
            }

            refugioDAO.insertar(new Refugio(nom, "", dir, tel));
            cargarListaRefugios();
            Toast.makeText(this, "Refugio insertado", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

    private void mostrarPantallaEliminar() {
        List<Refugio> refugios = refugioDAO.obtenerTodos();
        if (refugios.isEmpty()) {
            Toast.makeText(this, "No hay refugios para eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar Refugio");

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(40, 20, 40, 20);

        TextView tvMsg = new TextView(this);
        tvMsg.setText("Selecciona el refugio a eliminar:");
        tvMsg.setTextSize(16.0f);
        container.addView(tvMsg);

        Spinner spinner = new Spinner(this);
        ArrayAdapter<Refugio> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, refugios);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        container.addView(spinner);

        builder.setView(container);
        builder.setPositiveButton("Eliminar Refugio", (dialog, which) -> {
            Refugio rSel = (Refugio) spinner.getSelectedItem();
            if (rSel != null) {
                refugioDAO.eliminar(rSel.getIdRefugio());
                cargarListaRefugios();
                Toast.makeText(this, "Refugio eliminado", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void mostrarDetallesRefugio(Refugio refugio) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Detalles del Refugio");

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(40, 20, 40, 20);

        TextView tvInfo = new TextView(this);
        tvInfo.setText("Nombre: " + refugio.getNombre() + "\n"
                + "Dirección: " + refugio.getDireccion() + "\n"
                + "Teléfono: " + refugio.getTelefono());
        tvInfo.setTextSize(16.0f);
        container.addView(tvInfo);

        builder.setView(container);
        builder.setNeutralButton("Modificar Datos", (dialog, which) -> mostrarFormularioEditar(refugio));
        builder.setNegativeButton("Cerrar", null);
        builder.show();
    }

    private void mostrarFormularioEditar(Refugio refugio) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Refugio");

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(40, 20, 40, 20);

        EditText etNombre = new EditText(this);
        etNombre.setText(refugio.getNombre());
        container.addView(etNombre);

        EditText etDireccion = new EditText(this);
        etDireccion.setText(refugio.getDireccion());
        container.addView(etDireccion);

        EditText etTel = new EditText(this);
        etTel.setText(refugio.getTelefono());
        etTel.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        container.addView(etTel);

        builder.setView(container);
        builder.setPositiveButton("Guardar Cambios", null);
        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nom = etNombre.getText().toString().trim();
            String dir = etDireccion.getText().toString().trim();
            String tel = etTel.getText().toString().trim();

            if (nom.isEmpty() || dir.isEmpty() || tel.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios (Nombre, Dirección y Teléfono)", Toast.LENGTH_SHORT).show();
                return;
            }

            refugio.setNombre(nom);
            refugio.setDireccion(dir);
            refugio.setTelefono(tel);

            refugioDAO.actualizar(refugio);
            cargarListaRefugios();
            Toast.makeText(this, "Refugio actualizado", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }
}
