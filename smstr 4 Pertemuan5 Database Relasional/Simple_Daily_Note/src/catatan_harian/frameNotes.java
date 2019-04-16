package catatan_harian;

import java.awt.Frame;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ISMYNR
 */
public class frameNotes extends javax.swing.JFrame {

    DefaultTableModel modelTable;
    DefaultListModel modelList = new DefaultListModel();
    Object indexRow = "";
    Object indexItem = "";
    List<String> listID = new ArrayList<>();
    /**
     * Creates new form frameNotes
     */
    public frameNotes() {
        initComponents();
        koneksi.sambungDB();
        aturModelTabel();
        setMain();
        ambilDB();
        ambilListDB();
        this.setExtendedState(Frame.MAXIMIZED_BOTH);
        
    }
    private void setMain(){
        listChecklist.setFixedCellHeight(50);
        jdialogChecklist.setSize(500, 135);
        jdialogChecklist.setLocationRelativeTo(this);
        lb_Lcari.setVisible(false);
        txtf_Lcari.setVisible(false);
        lb_Ncari.setVisible(false);
        txtf_Ncari.setVisible(false);
        btn_Nkembali.setVisible(false);
        btn_Lkembali.setVisible(false);
        viewNotes.setSize(1000, 400);
        viewNotes.setLocationRelativeTo(this);
    }
    private void aturModelTabel(){
        Object[] field = {"ID", "Judul", "Diubah", "Dibuat"};
        modelTable = new DefaultTableModel(field,0) {
            boolean[] canEdit = new boolean [] {false, false, false, false};
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        };
        tblNotes.setModel(modelTable);
        tblNotes.setRowHeight(50);
        for (int i = 2; i <= 3; i++) {
            tblNotes.getColumnModel().getColumn(i).setMaxWidth(170);
            tblNotes.getColumnModel().getColumn(i).setMinWidth(170);}
        tblNotes.getColumnModel().getColumn(0).setMaxWidth(60);
    }
    public void ambilListDB(){
        modelList.removeAllElements();
        listID.clear();
        String sql = "SELECT id, text_list FROM checklist";
        Connection conn;
        Statement st;
        ResultSet rs;
        try {
            conn = koneksi.sambungDB();
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                modelList.addElement(rs.getString("text_list"));
                listID.add(rs.getString("id"));
            }
            listChecklist.setModel(modelList);
            st.close();
            conn.close();
            tblNotes.revalidate();
            tblNotes.repaint();
        } catch (Exception e) {
        }
    }
    public void getIdListItemDB(Object id){
        String sql = "SELECT id, text_list FROM checklist WHERE id='"+id+"'";
        Connection conn;
        Statement st;
        ResultSet rs;
        try {
            conn = koneksi.sambungDB();
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {   
                txtf_LADD_LUP.setText(rs.getString("text_list"));
            }
            st.close();
            conn.close();
            indexItem = id;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "getIdListItemDB(): "+e.getMessage());
        }
    }
    public void tambahListDB(){
        Object checklist = txtf_LADD_LUP.getText();
        tambahUbahHapusDB("INSERT INTO checklist (text_list) VALUES ('"+checklist+"')");
    }
    public void ubahListDB(Object i){
        Object checklist = txtf_LADD_LUP.getText();
        tambahUbahHapusDB("UPDATE checklist SET text_list = '" +checklist+ "' "
                + "WHERE id='" +i+ "'");
    }
    public void hapusListDB(String index){
        tambahUbahHapusDB("DELETE FROM checklist WHERE id='"+index+"'");
    }
    public void tambahUbahHapusDB(String sql){ // AGAR SIMPLE 
        Connection conn; // MENGGUNAKAN 1 METHOD KARENA HANYA MENJALANKAN SATU PROSES
        Statement st;
        try {
            conn = koneksi.sambungDB();
            st = conn.createStatement();
            st.executeUpdate(sql);
            conn.close();
            st.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "tambahUbahHapusDB(): "+e.getMessage());
        }
    }
    public void ambilDB(){
        modelTable.getDataVector().removeAllElements();
        String sql = "SELECT id, judul, tanggal_dibuat, terakhir_diubah FROM note";
        Connection conn;
        Statement st;
        ResultSet rs;
        
        try {
            conn = koneksi.sambungDB();
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                Object[] data = {rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(3)};
                modelTable.addRow(data);
            }
            st.close();
            conn.close();
            tblNotes.revalidate();
            tblNotes.repaint();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "ambilDB(): "+e.getMessage());
        }
    }
    public void getDataNotesDB(int i){
        Object ValueId = tblNotes.getValueAt(i, 0);
        String sql = "SELECT judul, isi_note FROM note WHERE id='" + ValueId + "'";
        Connection conn;
        Statement st;
        ResultSet rs;
        try {
            conn = koneksi.sambungDB();
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {                
                txtf_Judul_NADD_NUP.setText(rs.getString("judul"));
                txta_Isi_NADD_NUP.setText(rs.getString("isi_note"));
            }
            st.close();
            conn.close();
            tblNotes.revalidate();
            tblNotes.repaint();
            indexRow = ValueId; // nentuin idnya saat jdialog aktif
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "getIdNotesDB(): "+e.getMessage());
        }
    }
    public void tambahDataDB(){
        Object judul = txtf_Judul_NADD_NUP.getText();
        Object isi_note = txta_Isi_NADD_NUP.getText();
        tambahUbahHapusDB("INSERT INTO note (judul, isi_note, tanggal_dibuat, terakhir_diubah) "
            + "VALUES ('"+judul+"', '"+isi_note+"', NOW(), NOW())");
        tblNotes.revalidate();
        tblNotes.repaint();
    }
    public void ubahDataDB(Object i){
        Object judul = txtf_Judul_NADD_NUP.getText();
        Object isi_note = txta_Isi_NADD_NUP.getText();
        tambahUbahHapusDB("UPDATE note SET   = '" +judul+ "', isi_note = '" +isi_note+ "', "
                + "terakhir_diubah = NOW() WHERE id='" +i+ "'");
        tblNotes.revalidate();
        tblNotes.repaint();
    }
    public void hapusDataDB(int selRow){
        tambahUbahHapusDB("DELETE FROM note WHERE id='"+selRow+"'");
        tblNotes.revalidate();
        tblNotes.repaint();
    }
    public void tampilSplit(double a){
        areaSplit.setVisible(true);
        areaSplit.setDividerLocation(a);
    }
    public void editTable(boolean a){
        txta_Isi_NADD_NUP.setEditable(a);
        txtf_Judul_NADD_NUP.setEditable(a);
    }
    public void btnEnable(boolean a){
        btn_LADD.setEnabled(a);
        btn_LREM.setEnabled(a);
        lb_Lcari.setVisible(a);
        txtf_Lcari.setVisible(a);
        btn_NADD.setEnabled(!a);
        btn_NREM.setEnabled(!a);
        lb_Ncari.setVisible(!a);
        txtf_Ncari.setVisible(!a);
    }
    public void pencarianNotes(){
        modelTable.getDataVector().removeAllElements();
        String sql = "SELECT * FROM note WHERE id LIKE '%"+txtf_Ncari.getText()+"%' "
                + "OR judul LIKE '%"+txtf_Ncari.getText()+"%' "
                + "OR isi_note LIKE '%"+txtf_Ncari.getText()+"%' "
                + "OR tanggal_dibuat LIKE '%"+txtf_Ncari.getText()+"%' "
                + "OR terakhir_diubah LIKE '%"+txtf_Ncari.getText()+"%' ";
        Connection conn;
        Statement st;
        ResultSet rs;
        try {
            conn = koneksi.sambungDB();
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            while(rs.next()){
                modelTable.addRow(new Object[]{rs.getString(1), rs.getString(2), 
                    rs.getString(4), rs.getString(5)});
            }
            tblNotes.setModel(modelTable);
            conn.close();
            st.close();
            rs.close();
            tblNotes.revalidate();
            tblNotes.repaint();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "pencarianNotes" + e.getMessage());
        }
    }
     public void pencarianListItem(){
        modelList.removeAllElements();
        listID.clear();
        String sql = "SELECT * FROM checklist WHERE text_list LIKE '%"+txtf_Lcari.getText()+"%' ";
        Connection conn;
        Statement st;
        ResultSet rs;
        try {
            conn = koneksi.sambungDB();
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            while(rs.next()){
                modelList.addElement(rs.getString(2));
                listID.add(rs.getString(1));
            }
            listChecklist.setModel(modelList);
            conn.close();
            st.close();
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "pencarianNotes" + e.getMessage());
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        viewNotes = new javax.swing.JDialog();
        jPanel2 = new javax.swing.JPanel();
        btn_NADD_NUP = new javax.swing.JButton();
        txtf_Judul_NADD_NUP = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txta_Isi_NADD_NUP = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        lb_NADD_NUP = new javax.swing.JLabel();
        jdialogChecklist = new javax.swing.JDialog();
        jPanel4 = new javax.swing.JPanel();
        lb_LADD_LUP = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        btn_LADD_LUP = new javax.swing.JButton();
        txtf_LADD_LUP = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        btn_NOTES = new javax.swing.JButton();
        btn_LISTITEM = new javax.swing.JButton();
        btn_NADD = new javax.swing.JButton();
        btn_NREM = new javax.swing.JButton();
        btn_LREM = new javax.swing.JButton();
        btn_LADD = new javax.swing.JButton();
        txtf_Lcari = new javax.swing.JTextField();
        lb_Lcari = new javax.swing.JLabel();
        txtf_Ncari = new javax.swing.JTextField();
        lb_Ncari = new javax.swing.JLabel();
        btn_Nkembali = new javax.swing.JButton();
        btn_Lkembali = new javax.swing.JButton();
        areaSplit = new javax.swing.JSplitPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblNotes = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        listChecklist = new javax.swing.JList<>();

        jPanel2.setBackground(new java.awt.Color(0, 210, 211));

        btn_NADD_NUP.setBackground(new java.awt.Color(255, 255, 255));
        btn_NADD_NUP.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btn_NADD_NUP.setText("XXXXXX");
        btn_NADD_NUP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NADD_NUPActionPerformed(evt);
            }
        });

        txtf_Judul_NADD_NUP.setEditable(false);
        txtf_Judul_NADD_NUP.setFont(new java.awt.Font("Monospaced", 1, 16)); // NOI18N
        txtf_Judul_NADD_NUP.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtf_Judul_NADD_NUP.setText("XXXXXX");

        txta_Isi_NADD_NUP.setEditable(false);
        txta_Isi_NADD_NUP.setColumns(20);
        txta_Isi_NADD_NUP.setRows(5);
        txta_Isi_NADD_NUP.setText("XXXXXXX");
        jScrollPane1.setViewportView(txta_Isi_NADD_NUP);

        jPanel3.setBackground(new java.awt.Color(1, 163, 164));

        lb_NADD_NUP.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lb_NADD_NUP.setForeground(new java.awt.Color(255, 255, 255));
        lb_NADD_NUP.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb_NADD_NUP.setText("XXXXXX");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lb_NADD_NUP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lb_NADD_NUP, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtf_Judul_NADD_NUP)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_NADD_NUP, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtf_Judul_NADD_NUP, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE))
                    .addComponent(btn_NADD_NUP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        viewNotes.getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel4.setBackground(new java.awt.Color(1, 163, 164));
        jPanel4.setPreferredSize(new java.awt.Dimension(650, 50));

        lb_LADD_LUP.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lb_LADD_LUP.setForeground(new java.awt.Color(255, 255, 255));
        lb_LADD_LUP.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb_LADD_LUP.setText("XXXXXXXXXXXXXXXX");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lb_LADD_LUP, javax.swing.GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lb_LADD_LUP, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
        );

        jdialogChecklist.getContentPane().add(jPanel4, java.awt.BorderLayout.PAGE_START);

        jPanel5.setBackground(new java.awt.Color(0, 210, 211));
        jPanel5.setLayout(new java.awt.BorderLayout());

        btn_LADD_LUP.setPreferredSize(new java.awt.Dimension(100, 9));
        btn_LADD_LUP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_LADD_LUPActionPerformed(evt);
            }
        });
        jPanel5.add(btn_LADD_LUP, java.awt.BorderLayout.LINE_END);
        jPanel5.add(txtf_LADD_LUP, java.awt.BorderLayout.CENTER);

        jdialogChecklist.getContentPane().add(jPanel5, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(1, 163, 164));
        jPanel1.setPreferredSize(new java.awt.Dimension(720, 70));

        btn_NOTES.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btn_NOTES.setText("Notes");
        btn_NOTES.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NOTESActionPerformed(evt);
            }
        });

        btn_LISTITEM.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btn_LISTITEM.setText("List Item");
        btn_LISTITEM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_LISTITEMActionPerformed(evt);
            }
        });

        btn_NADD.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btn_NADD.setText("ADD");
        btn_NADD.setEnabled(false);
        btn_NADD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NADDActionPerformed(evt);
            }
        });

        btn_NREM.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btn_NREM.setText("REMOVE");
        btn_NREM.setEnabled(false);
        btn_NREM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NREMActionPerformed(evt);
            }
        });

        btn_LREM.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btn_LREM.setText("REMOVE");
        btn_LREM.setEnabled(false);
        btn_LREM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_LREMActionPerformed(evt);
            }
        });

        btn_LADD.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btn_LADD.setText("ADD");
        btn_LADD.setEnabled(false);
        btn_LADD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_LADDActionPerformed(evt);
            }
        });

        txtf_Lcari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtf_LcariKeyReleased(evt);
            }
        });

        lb_Lcari.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lb_Lcari.setForeground(new java.awt.Color(255, 255, 255));
        lb_Lcari.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb_Lcari.setText("CARI");

        txtf_Ncari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtf_NcariActionPerformed(evt);
            }
        });
        txtf_Ncari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtf_NcariKeyReleased(evt);
            }
        });

        lb_Ncari.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lb_Ncari.setForeground(new java.awt.Color(255, 255, 255));
        lb_Ncari.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb_Ncari.setText("CARI");

        btn_Nkembali.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btn_Nkembali.setText("<-");
        btn_Nkembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NkembaliActionPerformed(evt);
            }
        });

        btn_Lkembali.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btn_Lkembali.setText("->");
        btn_Lkembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_LkembaliActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(btn_NOTES, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_NADD)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_NREM)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtf_Ncari)
                    .addComponent(lb_Ncari, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_Nkembali)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 248, Short.MAX_VALUE)
                .addComponent(btn_Lkembali)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtf_Lcari)
                    .addComponent(lb_Lcari, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_LADD)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_LREM)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_LISTITEM, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btn_LISTITEM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btn_LREM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btn_LADD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btn_NADD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btn_NOTES, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btn_NREM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_Lkembali, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_Nkembali, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lb_Lcari)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtf_Lcari))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lb_Ncari)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtf_Ncari, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        areaSplit.setDividerSize(12);
        areaSplit.setOneTouchExpandable(true);

        tblNotes.setBackground(new java.awt.Color(204, 204, 204));
        tblNotes.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        tblNotes.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tblNotes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblNotes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblNotesMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblNotes);

        areaSplit.setLeftComponent(jScrollPane3);

        listChecklist.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 16)); // NOI18N
        listChecklist.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listChecklistMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(listChecklist);

        areaSplit.setRightComponent(jScrollPane4);

        getContentPane().add(areaSplit, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_NOTESActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NOTESActionPerformed
        tampilSplit(0.9);
        btnEnable(false);
    }//GEN-LAST:event_btn_NOTESActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        areaSplit.setVisible(false);
    }//GEN-LAST:event_formComponentResized

    private void btn_LISTITEMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_LISTITEMActionPerformed
        tampilSplit(0.1);
        btnEnable(true);
    }//GEN-LAST:event_btn_LISTITEMActionPerformed

    private void tblNotesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblNotesMouseClicked
        lb_NADD_NUP.setText("Ubah Catatan Saya");
        int ambilBaris = tblNotes.getSelectedRow();
        if(evt.getClickCount() == 2 && ambilBaris != -1){
            btn_NADD_NUP.setText("EDIT");
            viewNotes.setVisible(true);
            editTable(false);
            getDataNotesDB(ambilBaris);
            tblNotes.clearSelection();
        }
    }//GEN-LAST:event_tblNotesMouseClicked

    private void btn_NADD_NUPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NADD_NUPActionPerformed
        switch (btn_NADD_NUP.getText()) {
            case "EDIT":
                editTable(true);
                btn_NADD_NUP.setText("SIMPAN");
                break;
            case "SIMPAN":
                ubahDataDB(indexRow);
                indexRow = "";
                viewNotes.setVisible(false);
                JOptionPane.showMessageDialog(this, "Note Disimpan !");
                ambilDB();
                break;
            case "TAMBAH":
                tambahDataDB();
                btn_NADD_NUP.setText("");
                editTable(false);
                viewNotes.setVisible(false);
                JOptionPane.showMessageDialog(this, "Catatan ditambahkan");
                ambilDB();
                break;
            default:
                break;
        }
    }//GEN-LAST:event_btn_NADD_NUPActionPerformed

    private void btn_NADDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NADDActionPerformed
        txtf_Judul_NADD_NUP.setText("Judul Catatan");
        txta_Isi_NADD_NUP.setText("Isi Catatan");
        txtf_Judul_NADD_NUP.addFocusListener(new FocusListener() {
            @Override public void focusLost(final FocusEvent pE) {}
            @Override public void focusGained(final FocusEvent pE) {txtf_Judul_NADD_NUP.selectAll();}
        }); txta_Isi_NADD_NUP.addFocusListener(new FocusListener() {
            @Override public void focusLost(final FocusEvent pE) {}
            @Override public void focusGained(final FocusEvent pE) {txta_Isi_NADD_NUP.selectAll();}
        });
        lb_NADD_NUP.setText("Tambah Catatan Saya");
        btn_NADD_NUP.setText("TAMBAH");
//        txtf_Judul_NADD_NUP.setText("");
//        txta_Isi_NADD_NUP.setText("");
        editTable(true);
        viewNotes.setVisible(true);
    }//GEN-LAST:event_btn_NADDActionPerformed

    private void btn_NREMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NREMActionPerformed
        if (tblNotes.getSelectedRow() < 1) {
            JOptionPane.showMessageDialog(this, "pilih row yang ingin dihapus");
        }else{
            int result = JOptionPane.showConfirmDialog(this, "Yakin ingin hapus row yang dipilih ?");
            if (result == JOptionPane.YES_OPTION) {
                // remove notes in table and datase
                int selRow = tblNotes.getSelectedRow();
                Object ValueId = tblNotes.getValueAt(selRow, 0);
                hapusDataDB((int) ValueId);
                ambilDB();
            }
        }
    }//GEN-LAST:event_btn_NREMActionPerformed

    private void listChecklistMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listChecklistMouseClicked
        lb_LADD_LUP.setText("Ubah Item");
        int getIndexItem = listChecklist.getSelectedIndex();
        String getIdInList = listID.get(getIndexItem);
        JList a = (JList) evt.getSource();
         
        if(evt.getClickCount() == 2){
            btn_LADD_LUP.setText("EDIT");
            jdialogChecklist.setVisible(true);
            txtf_LADD_LUP.setEditable(false);
            getIdListItemDB(getIdInList); //proses update
        }
    }//GEN-LAST:event_listChecklistMouseClicked

    private void btn_LADDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_LADDActionPerformed
        jdialogChecklist.setVisible(true);
        txtf_LADD_LUP.setEditable(true);
        txtf_LADD_LUP.setText("");
        btn_LADD_LUP.setText("TAMBAH");
        lb_LADD_LUP.setText("Tambah Item");
    }//GEN-LAST:event_btn_LADDActionPerformed

    private void btn_LREMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_LREMActionPerformed
        int getIndexList = listChecklist.getSelectedIndex();
        if (getIndexList < 0) {
            JOptionPane.showMessageDialog(this, "pilih row yang ingin dihapus");
        }else{
            int result = JOptionPane.showConfirmDialog(this, "Yakin ingin hapus row yang dipilih ?");
            if (result == JOptionPane.YES_OPTION) {
                // remove Item in JList
                String getIdDBInList = listID.get(getIndexList);
                hapusListDB(getIdDBInList);
                ambilListDB();
            }
        }
    }//GEN-LAST:event_btn_LREMActionPerformed

    private void btn_LADD_LUPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_LADD_LUPActionPerformed
        switch (btn_LADD_LUP.getText()) {
            case "EDIT":
                btn_LADD_LUP.setText("SIMPAN");
                txtf_LADD_LUP.setEditable(true);
                break;
            case "SIMPAN":
                ubahListDB(indexItem);
                indexItem = "";
                jdialogChecklist.setVisible(false);
                JOptionPane.showMessageDialog(this, "Item Disimpan");
                ambilListDB();
                break;
            case "TAMBAH":
                tambahListDB();
                jdialogChecklist.setVisible(false);
                JOptionPane.showMessageDialog(this, "Item Ditambahkan");
                ambilListDB();
                break;
            default:
                break;
        }
        
    }//GEN-LAST:event_btn_LADD_LUPActionPerformed

    private void txtf_NcariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtf_NcariKeyReleased
        pencarianNotes();
        btn_Nkembali.setVisible(true);
    }//GEN-LAST:event_txtf_NcariKeyReleased

    private void txtf_LcariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtf_LcariKeyReleased
        pencarianListItem();
        btn_Lkembali.setVisible(true);
    }//GEN-LAST:event_txtf_LcariKeyReleased

    private void btn_NkembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NkembaliActionPerformed
        ambilDB();
        txtf_Ncari.setText("");
        btn_Nkembali.setVisible(false);
    }//GEN-LAST:event_btn_NkembaliActionPerformed

    private void btn_LkembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_LkembaliActionPerformed
        ambilListDB();
        txtf_Lcari.setText("");
        btn_Lkembali.setVisible(false);
    }//GEN-LAST:event_btn_LkembaliActionPerformed

    private void txtf_NcariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtf_NcariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtf_NcariActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(frameNotes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frameNotes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frameNotes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frameNotes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frameNotes().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane areaSplit;
    private javax.swing.JButton btn_LADD;
    private javax.swing.JButton btn_LADD_LUP;
    private javax.swing.JButton btn_LISTITEM;
    private javax.swing.JButton btn_LREM;
    private javax.swing.JButton btn_Lkembali;
    private javax.swing.JButton btn_NADD;
    private javax.swing.JButton btn_NADD_NUP;
    private javax.swing.JButton btn_NOTES;
    private javax.swing.JButton btn_NREM;
    private javax.swing.JButton btn_Nkembali;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JDialog jdialogChecklist;
    private javax.swing.JLabel lb_LADD_LUP;
    private javax.swing.JLabel lb_Lcari;
    private javax.swing.JLabel lb_NADD_NUP;
    private javax.swing.JLabel lb_Ncari;
    private javax.swing.JList<String> listChecklist;
    private javax.swing.JTable tblNotes;
    private javax.swing.JTextArea txta_Isi_NADD_NUP;
    private javax.swing.JTextField txtf_Judul_NADD_NUP;
    private javax.swing.JTextField txtf_LADD_LUP;
    private javax.swing.JTextField txtf_Lcari;
    private javax.swing.JTextField txtf_Ncari;
    private javax.swing.JDialog viewNotes;
    // End of variables declaration//GEN-END:variables
    
}

