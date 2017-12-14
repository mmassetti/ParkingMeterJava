import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.mysql.jdbc.Statement;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import javax.swing.LayoutStyle.ComponentPlacement;

public class VentanaPrincipal extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton btnAdmin;
	private java.sql.Connection conexion;
	private JLabel lblSistemaDeParquimetros;
	private VentanaAdmin ventanaAdmin;
	private VentanaInspector ventanaInspector;
	private VentanaSimulacion ventanaSimulacion;
	private JButton btnInspector;
	private JButton btnSimulacionTarjetaParquimetro;
	private JLabel lblParquimetros;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VentanaPrincipal frame = new VentanaPrincipal();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public VentanaPrincipal() {
		setTitle("Sistema de Parquimetros");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 750, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		setResizable(false);

		agregarComponentes();
		setListeners();
		conectarBD();

	}

	private void agregarComponentes() {
		btnAdmin = new JButton("Admin");
		btnAdmin.setFont(new Font("Calibri", Font.BOLD, 14));
		btnAdmin.setFocusPainted(false);
		
		lblSistemaDeParquimetros = new JLabel("Sistema de Parquimetros");
		lblSistemaDeParquimetros.setFont(new Font("Calibri", Font.BOLD, 28));
		
		btnInspector = new JButton("Inspector");
		btnInspector.setFont(new Font("Calibri", Font.BOLD, 14));
		btnInspector.setFocusPainted(false);
		
		
		//Imagenes
		//Admin
		URL url = VentanaPrincipal.class.getResource("/imagenes/admin.png");
		ImageIcon icono = new ImageIcon(url);
		
		JLabel lblAdmin = new JLabel(icono);
		
		
		/////////////
		//Inspector
		url = VentanaPrincipal.class.getResource("/imagenes/user.png");
		icono = new ImageIcon(url);
		JLabel lblInspector = new JLabel(icono);
		
		//Principal
		url = VentanaPrincipal.class.getResource("/imagenes/parquimetro.png");
		icono = new ImageIcon(url);
		
		lblParquimetros = new JLabel(icono);
		
		btnSimulacionTarjetaParquimetro = new JButton("Simulacion tarjeta-parquimetro");
		btnSimulacionTarjetaParquimetro.setFont(new Font("Calibri", Font.BOLD, 14));
		btnSimulacionTarjetaParquimetro.setFocusPainted(false);
		
		
		
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(216)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblInspector)
								.addComponent(lblAdmin))
							.addGap(52)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(btnInspector)
								.addComponent(btnAdmin, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(170)
							.addComponent(lblSistemaDeParquimetros)
							.addGap(28)
							.addComponent(lblParquimetros))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(207)
							.addComponent(btnSimulacionTarjetaParquimetro, GroupLayout.PREFERRED_SIZE, 234, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(180, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblParquimetros)
							.addContainerGap())
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(15)
							.addComponent(lblSistemaDeParquimetros)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(31)
									.addComponent(lblAdmin))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(44)
									.addComponent(btnAdmin, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)))
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
									.addComponent(lblInspector))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(42)
									.addComponent(btnInspector, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)))
							.addGap(98)
							.addComponent(btnSimulacionTarjetaParquimetro, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
							.addGap(81))))
		);
		contentPane.setLayout(gl_contentPane);
	}

	private void setListeners() {
		//agreagmos oyente para el login de admin
		btnAdmin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JPasswordField pf = new JPasswordField();
	
				int okCxl = JOptionPane.showConfirmDialog(null, pf, "Ingrese su password", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);

				if (okCxl == JOptionPane.OK_OPTION) {
					String password = new String(pf.getPassword());
					consultaAdmin(password);
				}
				
			}
		});
		
		//agreamos oyente para el login de inspector
		btnInspector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				JTextField user = new JTextField();
				int okCxl = JOptionPane.showConfirmDialog(null, user, "Ingrese su legajo", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);

				if (okCxl == JOptionPane.OK_OPTION) {
					String usuario = new String(user.getText());
					JPasswordField pf = new JPasswordField();
					int okCxl2 = JOptionPane.showConfirmDialog(null, pf, "Ingrese su password", JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.PLAIN_MESSAGE);

					if (okCxl2 == JOptionPane.OK_OPTION) {
						String password = new String(pf.getPassword());
						consultaInspector(usuario,password);
					}
				}
				
				
				
			}
		});
		
		//agregamos oyente para el boton simulacion tarjeta-paquimetro
		
		btnSimulacionTarjetaParquimetro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				irVentanaSimulacion();
			}
		});
	
	}
	
	private void conectarBD() {
		try
        {
		//Intento de conectar a la base de datos
		   String servidor = "localhost:3306";
           String baseDatos = "parquimetros";
           String url = "jdbc:mysql://" + servidor + "/" + baseDatos;
  
          conexion= DriverManager.getConnection(url, "inspector", "inspector");
          
          
        }
        catch (SQLException ex)
        {
            JOptionPane.showMessageDialog(this,
                                          "Se produjo un error al intentar conectarse a la base de datos.\n" + ex.getMessage(),
                                          "Error",
                                          JOptionPane.ERROR_MESSAGE);
            System.out.println("SQLExcepcion: " + ex.getMessage());
            System.out.println("SQLEstado: " + ex.getSQLState());
            System.out.println("CodigoError: " + ex.getErrorCode());
        }
        catch (Exception e)
        {
           e.printStackTrace();
        }
	}

	private void consultaAdmin (String pass) {
		 try
         {
        	String servidor = "localhost:3306";
            String baseDatos = "parquimetros";
            String uriConexion = "jdbc:mysql://" + servidor + "/" + baseDatos;
   
            //Intento de conexion a la base con las password ingresada para el admin
           conexion =DriverManager.getConnection(uriConexion, "admin", pass);
           //Cambio a la ventana para el admin
           ventanaAdmin= new VentanaAdmin(this,pass);
           ventanaAdmin.setVisible(true);
           cerrarVistaActual();
         }
         catch (Exception ex) {
        	 String msg= "Password incorrecta - Intente nuevamente";
        	 JOptionPane.showMessageDialog(new JFrame(), msg, "Error",JOptionPane.ERROR_MESSAGE);
         }
		
	}
	

	private boolean consultarLoginInspector(String legajo, String pass){
		String consulta="select * from inspectores where legajo='"+legajo+"' and password=md5('"+pass+"');";
		boolean respuesta=true;
		try{
			Statement s=(Statement) conexion.createStatement();
			s.executeQuery(consulta);
			ResultSet rs= s.getResultSet();
			respuesta=rs.next();
			
		}catch (SQLException ex)
	      {
	         // en caso de error, se muestra la causa en la consola
	         System.out.println("SQLException: " + ex.getMessage());
	         System.out.println("SQLState: " + ex.getSQLState());
	         System.out.println("VendorError: " + ex.getErrorCode());
	         JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
	                                       ex.getMessage() + "\n", 
	                                       "Error al Querer acceder.",
	                                       JOptionPane.ERROR_MESSAGE);
	         
	      }
		return respuesta;
	}

	private void consultaInspector (String legajo, String pass) {
		 try
         {
        	String servidor = "localhost:3306";
            String baseDatos = "parquimetros";
            String uriConexion = "jdbc:mysql://" + servidor + "/" + baseDatos;
   
            //Intento de conexion a la base con las password ingresada para el admin
            conexion = DriverManager.getConnection(uriConexion, "inspector", "inspector");
           
           	if(consultarLoginInspector(legajo,pass)){
        	   	cerrarVistaActual();
	           	//Cambio a la ventana para el admin
	           	ventanaInspector= new VentanaInspector(this,legajo,pass);
	           	ventanaInspector.setVisible(true);
         	}else{
          	 	String msg= "Legajo/Password incorrectos - Intente nuevamente";
          	 	JOptionPane.showMessageDialog(new JFrame(), msg, "Error",JOptionPane.ERROR_MESSAGE);        	   
           }
         }
         catch (Exception ex) {
        	 String msg= "Error al conectar la base de datos - Intente nuevamente";
        	 JOptionPane.showMessageDialog(new JFrame(), msg, "Error",JOptionPane.ERROR_MESSAGE);
         }
		
	}
	
	private void cerrarVistaActual() {
		cerrarBD();
		this.setVisible(false);
	}
	
	private void cerrarBD() {
		if (this.conexion != null)
	      {
	         try
	         {
	            conexion.close();
	            conexion = null;
	         }
	         catch (SQLException ex)
	         {
	            System.out.println("SQLExcepcion: " + ex.getMessage());
	            System.out.println("SQLEstado: " + ex.getSQLState());
	            System.out.println("CodigoError: " + ex.getErrorCode());
	         }
	      }
	}
	
	public void restaurar() {
		setVisible(true);
		conectarBD();
	}
	
	private void irVentanaSimulacion() {
		ventanaSimulacion= new VentanaSimulacion(this);
		ventanaSimulacion.setVisible(true);
		cerrarVistaActual();
	}
}