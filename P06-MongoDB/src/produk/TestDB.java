/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package produk;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.result.UpdateResult;
import java.util.Date;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author ISMYNR
 */
public class TestDB {
    public static void main(String[] args){
        try {
            //koneksi ke database MongoDB
            MongoDatabase database = Koneksi.sambungDB();
            
            //melihat daftar koleksi (tabel)
            System.out.println("\n=----------------------------=");
            System.out.println("Daftar Table dalam Database");
            System.out.println("=----------------------------=");
            MongoIterable<String> tables = database.listCollectionNames();
            for(String name : tables){
                System.out.println(name);
            }
            
            //menambah data
            System.out.println("\n=----------------------------=");
            System.out.println("Menambahkan data");
            System.out.println("=----------------------------=");
            MongoCollection<Document> col = database.getCollection("produk");
            Document doc = new Document();
            doc.put("nama", "Printer InkJet");
            doc.put("harga", 1204000);
            doc.put("tanggal", new Date());
            col.insertOne(doc);
            System.out.println("Data telah disimpan dalam koleksi");
            
            //mendapatkan _id dari dokumen yang baru saja diinsert
            System.out.println("\n=----------------------------=");
            System.out.println("Melihat id data yang baru diinput");
            System.out.println("=----------------------------=");
            ObjectId id = new ObjectId(doc.get("_id").toString());
            System.out.println("ID : "+id);
            
            //melihat atau menampilkan data
            System.out.println("\n=----------------------------=");
            System.out.println("Melihat dan menampilkan data");
            System.out.println("=----------------------------=");
            MongoCursor<Document> cursor = col.find().iterator();
            while(cursor.hasNext()){
                System.out.println(cursor.next().toJson());
            }
            
            //mencari dokumen berdasarkan id 
            Document myDoc = col.find(eq("_id", id)).first();
            System.out.println("\n=----------------------------=");
            System.out.println("Pencarian data berdasarkan id: "+id);
            System.out.println("=----------------------------=");
            System.out.println(myDoc.toJson());
            
            //mengedit data
            Document docs = new Document();
            docs.put("nama", "Canon");
            Document doc_edit = new Document("$set", docs);
            UpdateResult hasil_edit = col.updateOne(eq("_id", id), doc_edit);
            System.out.println(hasil_edit.getModifiedCount());
            
            //melihat atau menampilkan data
            System.out.println("\n=----------------------------=");
            System.out.println("Menampilkan data sekarang yang sudah diedit");
            System.out.println("=----------------------------=");
            cursor = col.find().iterator();
            while(cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
            
            //menghapus data
            col.deleteOne(eq("_id", id));
            System.out.println("=----------------------------=");
            System.out.println("Menghapus data");
            System.out.println("=----------------------------=");
            cursor = col.find().iterator();
            while(cursor.hasNext()){
                System.out.println(cursor.next().toJson());
            }
        } catch (Exception e) {
            System.err.println("Eror : "+e.getMessage());
        }
    }
}
