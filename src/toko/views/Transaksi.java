/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package toko.views;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Hadziq
 */
public class Transaksi extends javax.swing.JPanel {
 Connection conn;
    DefaultTableModel model;


    /**
     * Creates new form Transaksi
     */
    public Transaksi() {
        initComponents();
connectDatabase();
        model = new DefaultTableModel();
        tablePenjualan.setModel(model);
        model.addColumn("No");
        model.addColumn("Kode");
        model.addColumn("Barang");
        model.addColumn("Harga");
        model.addColumn("Jumlah");
        model.addColumn("Subtotal");

    }
     private void connectDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/login1";
            String user = "root";
            String password = "";
            conn = DriverManager.getConnection(url, user, password);
            JOptionPane.showMessageDialog(null, "Koneksi ke Database Berhasil");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Koneksi ke Database Gagal: " + e.getMessage());
        }
    }

      private void tambahBarang() {
        Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
        String kodeBarang = txtKodeBarang.getText();
        
        // Establish the database connection
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/login1", "root", "");

        // Prepare and execute the query
        String sql = "SELECT * FROM barang WHERE kode_barang = ?";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, kodeBarang);
        rs = pstmt.executeQuery();

        // Check if the item exists in the database
        if (rs.next()) {
            String namaBarang = rs.getString("nama_barang");
            double harga = rs.getDouble("harga_jual");
            int jumlah = Integer.parseInt(txtJumlah.getText());
            double subtotal = harga * jumlah;

            // Add the retrieved data to the table model
            model.addRow(new Object[]{model.getRowCount() + 1, kodeBarang, namaBarang, harga, jumlah, subtotal});
        } else {
            JOptionPane.showMessageDialog(null, "Item not found.");
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
    } finally {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error closing connection: " + e.getMessage());
        }
    }
     }
     
     
      private void simpanPenjualan() {
    if (txtNoPenjualan.getText().isEmpty() || txtTanggal.getText().isEmpty() || 
        Petugas.getText().isEmpty() || cmbPembayaran.getSelectedItem() == null) {
        JOptionPane.showMessageDialog(null, "Lengkapi semua data penjualan sebelum menyimpan.");
        return;
    }
    
    if (model.getRowCount() == 0) {
        JOptionPane.showMessageDialog(null, "Tidak ada barang yang ditambahkan.");
        return;
    }
    
    PreparedStatement pstPenjualan = null;
    PreparedStatement pstDetail = null;
    ResultSet rs = null;
    
    try {
        conn.setAutoCommit(false);
        
        // Menyimpan data penjualan
        String sqlPenjualan = "INSERT INTO penjualan (no_penjualan, tanggal, petugas, pembayaran) VALUES (?, ?, ?, ?)";
        pstPenjualan = conn.prepareStatement(sqlPenjualan, Statement.RETURN_GENERATED_KEYS);
        pstPenjualan.setString(1, txtNoPenjualan.getText());
        
        // Parsing tanggal dari string ke java.sql.Date
        java.sql.Date sqlDate = java.sql.Date.valueOf(txtTanggal.getText());
        pstPenjualan.setDate(2, sqlDate);
        
        pstPenjualan.setString(3,Petugas.getText());
        pstPenjualan.setString(4, cmbPembayaran.getSelectedItem().toString());
        pstPenjualan.executeUpdate();
        
        // Mendapatkan ID penjualan yang baru saja disimpan
        rs = pstPenjualan.getGeneratedKeys();
        int idPenjualan = 0;
        if (rs.next()) {
            idPenjualan = rs.getInt(1);
        }
        
        // Menyimpan data detail penjualan
        String sqlDetail = "INSERT INTO penjualan_detail (id_penjualan, kode_barang, nama_barang, harga, jumlah, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
        pstDetail = conn.prepareStatement(sqlDetail);
        
        for (int i = 0; i < model.getRowCount(); i++) {
            pstDetail.setInt(1, idPenjualan);
            pstDetail.setString(2, (String) model.getValueAt(i, 1));
            pstDetail.setString(3, (String) model.getValueAt(i, 2));
            pstDetail.setDouble(4, (Double) model.getValueAt(i, 3));
            pstDetail.setInt(5, (Integer) model.getValueAt(i, 4));
            pstDetail.setDouble(6, (Double) model.getValueAt(i, 5));
            pstDetail.addBatch();
        }
        
        pstDetail.executeBatch();
        conn.commit();
        
        JOptionPane.showMessageDialog(null, "Data Penjualan Berhasil Disimpan");
        
    } catch (SQLException e) {
        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Rollback Error: " + ex.getMessage());
        }
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        
    } finally {
        // Mengembalikan auto-commit ke true
        try {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error setting auto-commit: " + e.getMessage());
        }
        
        // Menutup semua resources
        try {
            if (rs != null) rs.close();
            if (pstPenjualan != null) pstPenjualan.close();
            if (pstDetail != null) pstDetail.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error closing resources: " + e.getMessage());
        }
    }
}
 public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new Transaksi().setVisible(true);
        });
    }    
          

    
    
    
    
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtNoPenjualan = new javax.swing.JTextField();
        txtTanggal = new javax.swing.JTextField();
        Petugas = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtKodeBarang = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtJumlah = new javax.swing.JTextField();
        Tambah = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablePenjualan = new javax.swing.JTable();
        cmbPembayaran = new javax.swing.JComboBox<>();
        simpan = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("Data Penjualan");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("No penjualan");

        txtTanggal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTanggalActionPerformed(evt);
            }
        });

        Petugas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PetugasActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("Tanggal Penjualan");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("Petugas");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setText("Pembayaran");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel7.setText("Data barang");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setText("Barang / Kode");

        txtKodeBarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtKodeBarangActionPerformed(evt);
            }
        });

        jLabel9.setText("Jumlah");

        Tambah.setText("Tambah");
        Tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TambahActionPerformed(evt);
            }
        });

        tablePenjualan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "No", "Kode", "Barang", "Harga", "Jumlah", "subtotal"
            }
        ));
        jScrollPane1.setViewportView(tablePenjualan);

        cmbPembayaran.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tunai", "Non Tunai" }));

        simpan.setText("simpan");
        simpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simpanActionPerformed(evt);
            }
        });

        jButton3.setText("cetak");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(42, 42, 42)
                                                .addComponent(jLabel1))
                                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(73, 73, 73))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtTanggal)
                                    .addComponent(Petugas)
                                    .addComponent(txtNoPenjualan)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtKodeBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                                        .addComponent(jLabel9)
                                        .addGap(13, 13, 13))
                                    .addComponent(cmbPembayaran, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(18, 18, 18)
                        .addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(61, 61, 61)
                        .addComponent(Tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(381, 381, 381))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(simpan)
                .addGap(112, 112, 112)
                .addComponent(jButton3)
                .addGap(46, 46, 46))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNoPenjualan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtTanggal)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(Petugas)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbPembayaran)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(66, 66, 66)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(txtKodeBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(27, 27, 27)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(simpan)
                    .addComponent(jButton3))
                .addGap(59, 59, 59))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtTanggalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTanggalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTanggalActionPerformed

    private void txtKodeBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtKodeBarangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtKodeBarangActionPerformed

    private void simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simpanActionPerformed
        // TODO add your handling code here:
        simpanPenjualan();
 
        
    }//GEN-LAST:event_simpanActionPerformed

    private void TambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TambahActionPerformed
        // TODO add your handling code here:
    tambahBarang();
    }//GEN-LAST:event_TambahActionPerformed

    private void PetugasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PetugasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PetugasActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField Petugas;
    private javax.swing.JButton Tambah;
    private javax.swing.JComboBox<String> cmbPembayaran;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton simpan;
    private javax.swing.JTable tablePenjualan;
    private javax.swing.JTextField txtJumlah;
    private javax.swing.JTextField txtKodeBarang;
    private javax.swing.JTextField txtNoPenjualan;
    private javax.swing.JTextField txtTanggal;
    // End of variables declaration//GEN-END:variables

 

}
