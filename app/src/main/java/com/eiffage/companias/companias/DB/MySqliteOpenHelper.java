package com.eiffage.companias.companias.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.eiffage.companias.companias.Objetos.Averia;
import com.eiffage.companias.companias.Objetos.Documento;
import com.eiffage.companias.companias.Objetos.Foto;

import java.util.ArrayList;

public class MySqliteOpenHelper  extends SQLiteOpenHelper {

    Context context;
    private static final String DATABASE_NAME = "Pedidos";
    private static final int DATABASE_VERSION = 10;

    private static final String CREAR_TABLA_PEDIDO = "CREATE TABLE Pedido (" +
            "codigo TEXT PRIMARY KEY," +
            "descripcion TEXT, fecha TEXT, marco TEXT, coordenadas TEXT," +
            "localidad TEXT, empresa TEXT, fechaFinMeco TEXT)";

    private static final String CREAR_TABLA_TAREA = "CREATE TABLE Tarea (" +
            "cod_tarea TEXT PRIMARY KEY, descripcion TEXT," +
            "cod_recurso TEXT, cod_pedido TEXT," +
            "cargoRecurso TEXT, nombreRecurso TEXT, algunaFotoEnviada TEXT, creadaPor TEXT," +
            "FOREIGN KEY(cod_pedido) REFERENCES Pedido(codigo))";

    private static final String CREAR_TABLA_FOTO = "CREATE TABLE Foto (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, imagen TEXT, descripcion TEXT," +
            "categoria TEXT, subcategoria TEXT, fecha TEXT, hora TEXT, coordenadasFotos TEXT," +
            "idTarea TEXT, urlFoto TEXT," +
            "FOREIGN KEY(idTarea) REFERENCES Tarea(cod_tarea))";

    private static final String CREAR_TABLA_DOCUMENTOS = "CREATE TABLE Documento ( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, cod_pedido TEXT, rutaInterna TEXT, nombreQueSeMuestra TEXT, " +
            "categoria TEXT, FOREIGN KEY(cod_pedido) REFERENCES Pedido(codigo))";

    private static final String CREAR_TABLA_AVERIAS = "CREATE TABLE Averia ( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, cod_averia TEXT, cod_recurso TEXT, descripcion TEXT, gestor TEXT, observaciones TEXT, fecha TEXT, localidad TEXT)";

    private static final String CREAR_TABLA_EDITABLES = "CREATE TABLE Editable (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, rutaInterna TEXT, nombreQueSeMuestra TEXT, cod_pedido TEXT )";

    public MySqliteOpenHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREAR_TABLA_PEDIDO);
        db.execSQL(CREAR_TABLA_TAREA);
        db.execSQL(CREAR_TABLA_FOTO);
        db.execSQL(CREAR_TABLA_DOCUMENTOS);
        db.execSQL(CREAR_TABLA_AVERIAS);
        db.execSQL(CREAR_TABLA_EDITABLES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + "Pedido");
        db.execSQL("DROP TABLE IF EXISTS " + "Tarea");
        db.execSQL("DROP TABLE IF EXISTS " + "Foto");
        db.execSQL("DROP TABLE IF EXISTS " + "Documento");
        db.execSQL("DROP TABLE IF EXISTS " + "Averia");
        db.execSQL("DROP TABLE IF EXISTS " + "Editable");
        this.onCreate(db);
    }

    //---------- AVERÍAS ----------\\
    public void insertarAveria(SQLiteDatabase db, Averia averia){
        db.execSQL("INSERT INTO Averia (cod_averia, cod_recurso, descripcion, gestor, observaciones, fecha, localidad) VALUES" +
                "('" + averia.getCod_averia() + "','" + averia.getCod_recurso() + "','" + averia.getDescripcion() + "','" +
                averia.getGestor() + "','" + averia.getObservaciones() + "','" + averia.getFecha() + "','" + averia.getLocalidad() +"')" );
    }

    public Cursor getAveriasLocales(SQLiteDatabase db, String cod_recurso){
        Cursor c = db.rawQuery("SELECT cod_averia, descripcion, gestor, fecha, observaciones, localidad FROM Averia WHERE cod_recurso LIKE'" + cod_recurso + "'", null);
        return c;
    }

    public void borrarAverias(SQLiteDatabase db, String cod_recurso){
        db.execSQL("DELETE FROM Averia WHERE cod_recurso LIKE '" + cod_recurso + "'");
    }

    public void insertarEditable(SQLiteDatabase db, String rutaInterna, String nombreQueSeMuestra,  String cod_pedido){
        Cursor c = db.rawQuery("SELECT * FROM Editable WHERE cod_pedido LIKE '" + cod_pedido + "' AND nombreQueSeMuestra LIKE '" + nombreQueSeMuestra + "' AND rutaInterna LIKE '" + rutaInterna + "'", null);
        if(c.getCount() == 0){
            db.execSQL("INSERT OR REPLACE INTO Editable (rutaInterna, nombreQueSeMuestra, cod_pedido) VALUES ('" + rutaInterna + "','" + nombreQueSeMuestra + "','" + cod_pedido + "')");
        }
        Log.d("Editable", "Se ha insertado el documento: " + nombreQueSeMuestra);
    }

    public boolean hayEditables(SQLiteDatabase db, String cod_pedido){
        Cursor c = db.rawQuery("SELECT * FROM Editable WHERE cod_pedido LIKE '" + cod_pedido + "'", null);
        Log.d("Nº docs editados", c.getCount() + "");
        if(c.getCount() > 0){
            return true;
        }
        else return false;
    }

    public void borrarEditados(SQLiteDatabase db, String cod_pedido){
        db.execSQL("DELETE FROM Editable WHERE cod_pedido LIKE '" + cod_pedido + "'");
    }

    public void borrarEditado(SQLiteDatabase db, String url_interna){
        db.execSQL("DELETE FROM Editable WHERE rutaInterna LIKE '" + url_interna + "'");
    }

    public ArrayList<Documento> getEditables(SQLiteDatabase db, String cod_pedido){
        ArrayList<Documento> actuales = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM Editable WHERE cod_pedido LIKE '" + cod_pedido + "'", null);
        if(c.getCount() > 0){
            c.moveToFirst();
            do{
                String rutaInterna = c.getString(1);
                String nombreQueSeMuestra = c.getString(2);

                actuales.add(new Documento(cod_pedido, "Editado", rutaInterna, "-", nombreQueSeMuestra));
            }while(c.moveToNext());
        }
        c.close();
        return actuales;

        //id INTEGER PRIMARY KEY AUTOINCREMENT, rutaInterna TEXT, nombreQueSeMuestra TEXT, cod_pedido TEXT
    }



    public void insertarDocumento(SQLiteDatabase db, String cod_pedido, String rutaInterna, String nombreQueSeMuestra, String categoria){
        db.execSQL("INSERT INTO Documento (cod_pedido, rutaInterna, nombreQueSeMuestra, categoria)VALUES ( '" + cod_pedido + "', '" + rutaInterna + "', '" +
                nombreQueSeMuestra + "','" + categoria + "')");
        Cursor c = db.rawQuery("SELECT * FROM Documento WHERE cod_pedido LIKE '" + cod_pedido + "'", null);
        Log.d("INSERCION DOCUMENTO", "AHORA HAY " + c.getCount() + " FILAS EN LA TABLA DOCUMENTO");
        c.close();

    }

    public void borrarTodosDocumentosGenerales(SQLiteDatabase db){
        db.execSQL("DELETE FROM Documento WHERE categoria LIKE 'Docu_General_IBE'");
        Cursor c = db.rawQuery("SELECT * FROM Documento WHERE categoria LIKE 'Docu_General_IBE'", null);
        Log.d("BORRADO DOCUS GENERALES", "AHORA HAY " + c.getCount() + " DOCUMENTOS GENERALES");
        c.close();
    }

    public void borrarFicherosDePedido(SQLiteDatabase db, String cod_pedido, String categoria){
        db.execSQL("DELETE FROM Documento WHERE cod_pedido LIKE '" + cod_pedido + "' AND categoria LIKE '" + categoria + "'");
    }

    public ArrayList<Documento> getDocumentos(SQLiteDatabase db, String cod_pedido, String categoria){
        ArrayList<Documento> actuales = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM Documento WHERE cod_pedido LIKE '" + cod_pedido + "' and categoria LIKE '" + categoria + "'", null);
        if(c.getCount() > 0){
            c.moveToFirst();
            do{
                String pedido = c.getString(1);
                String cat = c.getString(4);
                String rutaLocal = c.getString(2);
                String url = null;
                String nom = c.getString(3);
                actuales.add(new Documento(pedido, cat, rutaLocal, url, nom));
            }while(c.moveToNext());
        }
        c.close();
        return actuales;
    }

    //----------Actualización del listado de tareas----------\\

    public void limpiarTablaPedidos(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + "Tarea");
        db.execSQL(CREAR_TABLA_TAREA);
    }

    //----------Se guarda un informe con fotos----------\\

    public int insertarFoto(SQLiteDatabase db, ContentValues c){
        int id = (int) db.insert("Foto", "-", c);
        return id;
    }

    //----------Se elimina la tarea porque se ha enviado con éxito, marcando la casilla de finalizar----------\\

    public void borrarTarea(SQLiteDatabase db, String idTarea){
        db.execSQL("DELETE FROM TAREA WHERE cod_tarea LIKE '" + idTarea + "'");
    }

    //----------Recuperar fotos de una tarea activa----------\\
    public Cursor recuperarFotos(SQLiteDatabase db, String idTarea){
        Cursor c = db.rawQuery("SELECT * FROM Foto WHERE idTarea LIKE '" + idTarea + "'", null);
        return c;
    }

    //----------Borrar todas las fotos de una tarea (se hace antes de guardar)----------\\
    public void borrarTodasLasFotosDeTarea(SQLiteDatabase db, String idTarea){
        db.execSQL("DELETE FROM Foto WHERE idTarea LIKE '" + idTarea + "'");
    }

    public void fotosEnviadas(SQLiteDatabase db, String idTarea){
        db.execSQL("UPDATE Tarea SET algunaFotoEnviada = 'true' WHERE cod_tarea LIKE '" + idTarea + "'");
    }

    public Foto getFoto(SQLiteDatabase db, String idFoto){
        Cursor c = db.rawQuery("SELECT * FROM Foto WHERE id = " + Integer.parseInt(idFoto), null);
        Foto foto = null;
        if(c == null) Log.d("Problemas", "El cursor ha muerto");
        else if(c.getCount() > 0){
            Log.d("Sí hay longitudes", "El id está bien comprobado");
            c.moveToFirst();
            int id = c.getInt(0);
            String descripcion = c.getString(2);
            String categoria = c.getString(3);
            String subcategoria = c.getString(4);
            String fecha = c.getString(5);
            String hora = c.getString(6);
            String coordenadas = c.getString(7);
            String idTarea = c.getString(8);
            String urlFoto = c.getString(9);

            foto = new Foto(id, categoria, subcategoria, fecha, hora, coordenadas, idTarea, descripcion, urlFoto);
        }
        else {
            Log.d("No hay longitudes", "El id está mal comprobado");
        }
        c.close();
        return foto;
    }

    public void actualizarValor(SQLiteDatabase db, String idFoto, String clave, String valor){
        db.execSQL("UPDATE Foto SET " + clave + " = '" + valor + "' WHERE id = " + Integer.parseInt(idFoto));
    }

    public void borrarFoto(SQLiteDatabase db, String idFoto){
        db.execSQL("DELETE FROM Foto WHERE id = " + Integer.parseInt(idFoto));
    }
}
