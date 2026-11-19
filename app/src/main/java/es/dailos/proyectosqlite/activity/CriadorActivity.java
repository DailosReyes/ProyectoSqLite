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
import es.dailos.proyectosqlite.dao.CriadorDAO;
import es.dailos.proyectosqlite.databinding.ActivityCriadorBinding;
import es.dailos.proyectosqlite.entity.Criador;

public class CriadorActivity extends AppCompatActivity {

    private ActivityCriadorBinding binding;
    private CriadorDAO criadorDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityCriadorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        criadorDAO = new CriadorDAO(this);

        binding.toolbarLayout.tvToolbarTitle.setText("Criadores");
        binding.toolbarLayout.btnBack.setOnClickListener(v -> finish());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        binding.btnInsertar.setOnClickListener(v -> mostrarFormularioInsertar());
        binding.btnEliminar.setOnClickListener(v -> mostrarPantallaEliminar());

        cargarListaCriadores();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarListaCriadores();
    }

    private void cargarListaCriadores() {
        List<Criador> criadores = criadorDAO.obtenerTodos();
        RecyclerViewAdapter<Criador> adapter = new RecyclerViewAdapter<>(
                criadores,
                R.drawable.ic_criador,
                new RecyclerViewAdapter.TextBinder<Criador>() {
                    @Override
                    public String getTitle(Criador c) {
                        return c.getNombre();
                    }

                    @Override
                    public String getSubtitle(Criador c) {
                        return (c.getTelefono() != null ? c.getTelefono() : "");
                    }
                },
                new RecyclerViewAdapter.OnItemClickListener<Criador>() {
                    @Override
                    public void onEdit(Criador item) {
                        mostrarFormularioEditar(item);
                    }

                    @Override
                    public void onInfo(Criador item) {
                        mostrarDetallesCriador(item);
                    }
                }
        );
        binding.recyclerView.setAdapter(adapter);
    }

    private void mostrarFormularioInsertar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Insertar Criador");

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(40, 20, 40, 20);

        EditText etNombre = new EditText(this);
        etNombre.setHint("Nombre");
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

            criadorDAO.insertar(new Criador(nom, dir, tel));
            cargarListaCriadores();
            Toast.makeText(this, "Criador insertado", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

    private void mostrarPantallaEliminar() {
        List<Criador> criadores = criadorDAO.obtenerTodos();
        if (criadores.isEmpty()) {
            Toast.makeText(this, "No hay criadores para eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar Criador");

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(40, 20, 40, 20);

        TextView tvMsg = new TextView(this);
        tvMsg.setText("Selecciona el criador a eliminar:");
        tvMsg.setTextSize(16.0f);
        container.addView(tvMsg);

        Spinner spinner = new Spinner(this);
        ArrayAdapter<Criador> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, criadores);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        container.addView(spinner);

        builder.setView(container);
        builder.setPositiveButton("Eliminar Criador", (dialog, which) -> {
            Criador cSel = (Criador) spinner.getSelectedItem();
            if (cSel != null) {
                criadorDAO.eliminar(cSel.getIdCriador());
                cargarListaCriadores();
                Toast.makeText(this, "Criador eliminado", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void mostrarDetallesCriador(Criador criador) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Detalles del Criador");

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(40, 20, 40, 20);

        TextView tvInfo = new TextView(this);
        tvInfo.setText("Nombre: " + criador.getNombre() + "\n"
                + "Dirección: " + criador.getDireccion() + "\n"
                + "Teléfono: " + criador.getTelefono());
        tvInfo.setTextSize(16.0f);
        container.addView(tvInfo);

        builder.setView(container);
        builder.setNeutralButton("Modificar Datos", (dialog, which) -> mostrarFormularioEditar(criador));
        builder.setNegativeButton("Cerrar", null);
        builder.show();
    }

    private void mostrarFormularioEditar(Criador criador) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Criador");

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(40, 20, 40, 20);

        EditText etNombre = new EditText(this);
        etNombre.setText(criador.getNombre());
        container.addView(etNombre);

        EditText etDireccion = new EditText(this);
        etDireccion.setText(criador.getDireccion());
        container.addView(etDireccion);

        EditText etTel = new EditText(this);
        etTel.setText(criador.getTelefono());
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

            criador.setNombre(nom);
            criador.setDireccion(dir);
            criador.setTelefono(tel);

            criadorDAO.actualizar(criador);
            cargarListaCriadores();
            Toast.makeText(this, "Criador actualizado", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }
}