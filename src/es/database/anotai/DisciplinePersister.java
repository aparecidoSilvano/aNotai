package es.database.anotai;

import java.util.ArrayList;
import java.util.List;

import es.model.anotai.Discipline;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DisciplinePersister implements AbstractPersister<Discipline> {
	
	private SQLiteDatabase db;
	
	private static final String TABLE_NAME = "disciplineTable";
	
	public DisciplinePersister(Context context){
		AnotaiDbHelper helper = new AnotaiDbHelper(context);
		db = helper.getWritableDatabase();
	}

	@Override
	public void create(Discipline target) {
		ContentValues values = new ContentValues();
		values.put("name", target.getName());
		values.put("teacher", target.getTeacher());
		
		db.insert(TABLE_NAME, null, values);
	}

	@Override
	public void update(Discipline target) {
		ContentValues values = new ContentValues();
		values.put("name", target.getName());
		values.put("teacher", target.getTeacher());
		
		db.update(TABLE_NAME, values, "_id = " + target.getId(), null);		
	}

	@Override
	public void delete(Discipline target) {
		db.delete(TABLE_NAME, "_id = " + target.getId(), null);
	}

	@Override
	public Discipline retrieveById(long id) {
		Discipline target = null;
		String[] columns = {"_id", "name", "teacher"};
		
		Cursor cursorDisc = db.query(TABLE_NAME, columns, "_id = " + id, null, null, null, null);
		
		if(cursorDisc.getCount() > 0){
			cursorDisc.moveToFirst();
			
			target = new Discipline();
			target.setId(cursorDisc.getLong(0));
			target.setName(cursorDisc.getString(1));
			target.setTeacher(cursorDisc.getString(2));
		}
		
		//TODO pegar tasks
		
		return target;
	}

	@Override
	public List<Discipline> retrieveAll() {
		List<Discipline> list = new ArrayList<Discipline>();
		String[] columns = {"_id, name, teacher"};
		
		Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, "name ASC");
		
		if(cursor.getCount() > 0){
			cursor.moveToFirst();
			
			do{
				
				Discipline target = new Discipline();
				target.setId(cursor.getLong(0));
				target.setName(cursor.getString(1));
				target.setTeacher(cursor.getString(2));
				//TODO pegar tasks
				list.add(target);
				
			} while(cursor.moveToNext());
		}
		
		return list;
	}
	
	
	

}
