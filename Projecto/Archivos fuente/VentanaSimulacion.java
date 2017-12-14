import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.mysql.jdbc.Statement;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.CallableStatement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JComboBox;

public class VentanaSimulacion extends JFrame {


	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private VentanaPrincipal ventanaPrincipal;
	private java.sql.Connection conexion;
	private JComboBox<String> cbCalle; 
	private JComboBox<String> cbAltura;
	private JComboBox<String> cbParquimetro;
	private JComboBox<String> cbTarjeta;
	private String calle,altura,parquimetro,tarjeta;
	private JButton btnSimular; 
	private CallableStatement stmt;

	public VentanaSimulacion(VentanaPrincipal padre) {
		ventanaPrincipal= padre;	
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 750, 519);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
		setResizable(false);
	
		agregarComponentes();
		conectarBD();
		
	
		agregarOpcionesCalle();
		cbCalle.setSelectedItem(null);
		cbAltura.removeAllItems();
		cbParquimetro.removeAllItems();
		cbTarjeta.removeAllItems();
		cbTarjeta.setSelectedItem(null);
		btnSimular.setEnabled(false);
	}
	
	private void agregarComponentes() {
		JButton btnVolver = new JButton("Volver");
		btnVolver.setFont(new Font("Calibri", Font.BOLD, 12));
		btnVolver.setFocusPainted(false);
		btnVolver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cerrarBD();
				dispose();
				ventanaPrincipal.restaurar();
			}
		});
		
		JLabel lblNewLabel = new JLabel("Simulacion de una tarjeta a cualquier parquimetro");
		lblNewLabel.setFont(new Font("Calibri", Font.BOLD, 20));
		
		JLabel lblSeleccionarCalle = new JLabel("Seleccionar calle");
		lblSeleccionarCalle.setFont(new Font("Calibri", Font.BOLD, 16));
		
		cbCalle = new JComboBox<String>();
		cbCalle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				calle = cbCalle.getItemAt(cbCalle.getSelectedIndex());
				cbAltura.removeAllItems();
				agregarOpcionesAltura();	
				
			}
		});
		
		JLabel lblSeleccionarAltura = new JLabel("Seleccionar altura");
		lblSeleccionarAltura.setFont(new Font("Calibri", Font.BOLD, 16));
		
		cbAltura = new JComboBox<String>();
		cbAltura.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				altura = cbAltura.getItemAt(cbAltura.getSelectedIndex());
				cbParquimetro.removeAllItems();
				agregarOpcionesParquimetros();		

			}
		});
		
		JLabel lblSeleccionarParquimetro = new JLabel("Seleccionar parquimetro");
		lblSeleccionarParquimetro.setFont(new Font("Calibri", Font.BOLD, 16));
		
		cbParquimetro = new JComboBox<String>();
		cbParquimetro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parquimetro= cbParquimetro.getItemAt(cbParquimetro.getSelectedIndex());
				agregarOpcionesTarjetas();
				//cbTarjeta.setSelectedItem(null);
				cbTarjeta.setSelectedIndex(-1);
			}
		});
		
		JLabel lblSeleccionarTarjeta = new JLabel("Seleccionar tarjeta");
		lblSeleccionarTarjeta.setFont(new Font("Calibri", Font.BOLD, 16));
		
		cbTarjeta = new JComboBox<String>();
		cbTarjeta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (cbTarjeta.getItemAt(1) != null) {
					btnSimular.setEnabled(true);
				}
				tarjeta= cbTarjeta.getItemAt(cbTarjeta.getSelectedIndex());
				
			}
		});
		
		btnSimular = new JButton("Simular conexion");
		btnSimular.setFocusPainted(false);
		btnSimular.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					stmt=conexion.prepareCall("{call conectar(?,?)}");
					stmt.setString(1,tarjeta);
					stmt.setString(2,parquimetro);					
					imprimirResultado(stmt);
					
					
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnSimular.setFont(new Font("Calibri", Font.BOLD, 16));
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnVolver)
					.addContainerGap(660, Short.MAX_VALUE))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap(144, Short.MAX_VALUE)
					.addComponent(lblNewLabel)
					.addGap(166))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(63)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblSeleccionarTarjeta, GroupLayout.DEFAULT_SIZE, 661, Short.MAX_VALUE)
							.addContainerGap())
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(cbParquimetro, GroupLayout.PREFERRED_SIZE, 155, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblSeleccionarParquimetro)
							.addContainerGap())
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(cbAltura, GroupLayout.PREFERRED_SIZE, 151, GroupLayout.PREFERRED_SIZE)
							.addGap(164)
							.addComponent(btnSimular)
							.addGap(205))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblSeleccionarAltura)
							.addContainerGap())
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
								.addComponent(cbCalle, Alignment.LEADING, 0, 148, Short.MAX_VALUE)
								.addComponent(lblSeleccionarCalle, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE))
							.addGap(523))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(cbTarjeta, GroupLayout.PREFERRED_SIZE, 156, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(25)
					.addComponent(lblNewLabel)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(26)
							.addComponent(lblSeleccionarCalle)
							.addGap(18)
							.addComponent(cbCalle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(27)
							.addComponent(lblSeleccionarAltura)
							.addGap(18)
							.addComponent(cbAltura, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(27)
							.addComponent(lblSeleccionarParquimetro)
							.addGap(18)
							.addComponent(cbParquimetro, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(18))
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(btnSimular)
							.addGap(80)))
					.addComponent(lblSeleccionarTarjeta)
					.addGap(18)
					.addComponent(cbTarjeta, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
					.addComponent(btnVolver)
					.addContainerGap())
		);
		contentPane.setLayout(gl_contentPane);
		
	}
	
	private void conectarBD() {
		try {
			String servidor = "localhost:3306";
			String baseDatos = "parquimetros";
			String url = "jdbc:mysql://" + servidor + "/" + baseDatos;
			conexion = DriverManager.getConnection(url, "parquimetro", "parq");

		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this,
					"Se produjo un error al intentar conectarse a la base de datos.\n" + ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	private void agregarOpcionesCalle(){
		String consulta="SELECT DISTINCT calle FROM parquimetros;";
		try{
			Statement s= (Statement) conexion.createStatement();
			s.executeQuery(consulta);
			ResultSet rs = s.getResultSet();
			while (rs.next()) {
				cbCalle.addItem(rs.getString(1).toString());
			}			
			
		}catch (SQLException ex){
	         // en caso de error, se muestra la causa en la consola
	         System.out.println("SQLException: " + ex.getMessage());
	         System.out.println("SQLState: " + ex.getSQLState());
	         System.out.println("VendorError: " + ex.getErrorCode());
	         JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),ex.getMessage() + "\n","Error al Querer acceder.",JOptionPane.ERROR_MESSAGE);	         
	    }catch(Exception e){
			System.out.println(e.getMessage());
		}
	}

	//agrega las opcions de altura según la calle ya seleccionada
		private void agregarOpcionesAltura(){
			String consulta="SELECT altura FROM parquimetros WHERE calle='"+calle+"';";
			try{
				Statement s= (Statement) conexion.createStatement();
				s.executeQuery(consulta);
				ResultSet rs = s.getResultSet();
				while (rs.next()) {
					cbAltura.addItem(rs.getString(1).toString());
				}		
				
			}catch (SQLException ex){
		         // en caso de error, se muestra la causa en la consola
		         System.out.println("SQLException: " + ex.getMessage());
		         System.out.println("SQLState: " + ex.getSQLState());
		         System.out.println("VendorError: " + ex.getErrorCode());
		         JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),ex.getMessage() + "\n","Error al Querer acceder.",JOptionPane.ERROR_MESSAGE);	         
		    }catch(Exception e){
				System.out.println(e.getMessage());
			}
		}
		
		//agrega las opciones de parquimetros según las opciones de calle y altura elegidas
		private void agregarOpcionesParquimetros(){
			String consulta="SELECT id_parq FROM parquimetros WHERE calle='"+calle+"'and altura='"+altura+"';";
			try{
				Statement s= (Statement) conexion.createStatement();
				s.executeQuery(consulta);
				ResultSet rs = s.getResultSet();
				while (rs.next()) {
					cbParquimetro.addItem(rs.getString(1).toString());
				}			
				
			}catch (SQLException ex){
		         // en caso de error, se muestra la causa en la consola
		         System.out.println("SQLException: " + ex.getMessage());
		         System.out.println("SQLState: " + ex.getSQLState());
		         System.out.println("VendorError: " + ex.getErrorCode());
		         JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),ex.getMessage() + "\n", "Error al Querer acceder.",JOptionPane.ERROR_MESSAGE);
		     
		    }catch(Exception e){
				System.out.println(e.getMessage());
			}
		}
		
		private void agregarOpcionesTarjetas() {
			String consulta= "SELECT DISTINCT * from tarjetas;";
			try{
				Statement s= (Statement) conexion.createStatement();
				s.executeQuery(consulta);
				ResultSet rs = s.getResultSet();
				while (rs.next()) {
					cbTarjeta.addItem(rs.getString(1).toString());
				}			
				
			}catch (SQLException ex){
		         // en caso de error, se muestra la causa en la consola
		         System.out.println("SQLException: " + ex.getMessage());
		         System.out.println("SQLState: " + ex.getSQLState());
		         System.out.println("VendorError: " + ex.getErrorCode());
		         JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),ex.getMessage() + "\n", "Error al Querer acceder.",JOptionPane.ERROR_MESSAGE);
		     
		    }catch(Exception e){
				System.out.println(e.getMessage());
			}		
		}
	
	 private void cerrarBD(){
			try
		      {
		         if (this.conexion != null)
		         {
		            conexion.close();
		            conexion = null;
		         } 
		        
		      }
		         catch (SQLException ex)
		         {
		            System.out.println("SQLExcepcion " + ex.getMessage());
		            System.out.println("SQLEstado: " + ex.getSQLState());
		            System.out.println("CodigoError: " + ex.getErrorCode());
		         }
		    
		}
	 
	 private void imprimirResultado(CallableStatement stmt) {
		String imprimir="";

		try {
			ResultSet resultSet = stmt.executeQuery();
			ResultSetMetaData rsmd = resultSet.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			while (resultSet.next()) {
			    for (int i = 1; i <= columnsNumber; i++) {
			        String columnValue = resultSet.getString(i);
			        imprimir+= rsmd.getColumnName(i)+": "+columnValue+"\n";
			    }
			    imprimir+=" ";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
		 JOptionPane.showMessageDialog (null, imprimir, "Simulacion tarjeta-parquimetro", JOptionPane.INFORMATION_MESSAGE);
		 	
	 }
	 

	
}
