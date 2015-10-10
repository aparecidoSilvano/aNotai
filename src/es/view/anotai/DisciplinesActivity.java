package es.view.anotai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import projeto.es.view.anotai.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import es.adapter.anotai.DisciplineAdapter;
import es.database.anotai.DisciplinePersister;
import es.database.anotai.TaskPersister;
import es.model.anotai.Discipline;

public class DisciplinesActivity extends Activity {
	private DisciplinePersister dPersister;
	private TaskPersister tPersister;
    private List<Discipline> disciplines;
    private ListView lvDisciplines;
    private DisciplineAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disciplines);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        tPersister = new TaskPersister(this);
        dPersister = new DisciplinePersister(this);
        disciplines = dPersister.retrieveAll();
        
        setOnClickAddDiscipline();
        
     // Recuperando a listview de disciplinas.
        lvDisciplines = (ListView) findViewById(R.id.activity_disciplines_lv_disciplines);
        adapter = new DisciplineAdapter(DisciplinesActivity.this, disciplines);
        lvDisciplines.setAdapter(adapter);
        lvDisciplines.setOnItemClickListener(new OnItemClickListener() {
    		@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Passar a disciplina clicada na intent que vai a tela da disciplina
				Intent intent = new Intent(DisciplinesActivity.this, DisciplineActivity.class);
				intent.putExtra("discipline", disciplines.get(position));
				startActivity(intent);
			}
		});
        
        
    }

	private void setOnClickAddDiscipline() {
		Button btAddDiscipline = (Button) findViewById(R.id.activity_disciplines_bt_add_discipline);
        btAddDiscipline.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(DisciplinesActivity.this);
                dialog.setContentView(R.layout.dialog_add_discipline);
                dialog.setTitle(R.string.new_discipline);

                // recuperando os elementos do dialog
                final EditText editNameContact = (EditText) dialog
                        .findViewById(R.id.dialog_add_disc_et_name_discipline);
                
                final Button btOk = (Button) dialog.findViewById(R.id.dialog_add_disc_bt_ok);
                final Button btCancel = (Button) dialog.findViewById(R.id.dialog_add_disc_bt_cancel);

                btOk.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(final View v) {
                        final String name = editNameContact.getText().toString();

                        if (name.isEmpty()) {
                         // Mostra mensagem de erro.
                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.invalid_name),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Discipline discipline = new Discipline(name, "");
                            dPersister.create(discipline);
                            disciplines.add(discipline);
                            Log.d(STORAGE_SERVICE, "Salva disciplina: " + name);
                            
                            // Atualiza a lista.
                            loadList(0);
                            dialog.dismiss();
                        }
                    }
                });

                btCancel.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(final View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
	}
    
    private void loadList(int OrderID) {
    	final int ALPHABETICAL_ORDER = 0;
		final int TOTAL_TASKS_ORDER = 1;
		
		switch(OrderID){
		case ALPHABETICAL_ORDER:
			Collections.sort(disciplines, new Comparator<Discipline>() {

				@Override
				public int compare(Discipline left, Discipline right) {
					return left.getName().compareTo(right.getName());
				}
			});
			break;
			
		case TOTAL_TASKS_ORDER:
			Collections.sort(disciplines, new Comparator<Discipline>(){

				@Override
				public int compare(Discipline left, Discipline right) {
					int leftTasks = tPersister.retrieveAll(left).size();
					int rightTasks = tPersister.retrieveAll(right).size();
					if(leftTasks > rightTasks){
						return 1;
					} else if(leftTasks < rightTasks){
						return -1;
					} else {
						return 0;
					}
				}
			});
			Collections.reverse(disciplines);
			break;
		}
    	
        adapter = new DisciplineAdapter(DisciplinesActivity.this, disciplines);
        lvDisciplines.setAdapter(adapter);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
    }

}
