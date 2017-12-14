import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;

import com.mysql.jdbc.Statement;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import javax.swing.JFormattedTextField;
import javax.swing.JComboBox;
import quick.dbtable.DBTable;
import javax.swing.SwingConstants;

public class VentanaInspector extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel panelPrincipal;
	private VentanaPrincipal ventanaPrincipal;
	private String legajo;
	private String calle;
	private String altura;
	private String parquimetro;
	private String id_asociado_con;
	private JButton btnVolver;
	private java.sql.Connection conexion;
	private ArrayList<String> patentes;
	private ArrayList<ArrayList<String>> multas;
	private GroupLayout panelPatente;
	private GroupLayout panelUbicacion;
	private GroupLayout panelMultas;
	private JFormattedTextField patenteLetras;
	private JFormattedTextField patenteNumeros;
	private JLabel titulo;
	private JLabel titulo2;
	private JButton agregarPatente;
	private JButton conectarParquimetro;
	private JButton btnGenerarMultas;
	private JLabel mensaje;
	private JComboBox<String> cbCalle;
	private JLabel lblCalle;
	private JComboBox<String> cbAltura;
	private JLabel lblAltura;
	private JComboBox<String> cbParquimetro;
	private JLabel lblParquimetro;
	private DBTable tablaMultas= new DBTable();
	private JButton btnEliminarPatente;		
	private JComboBox<String> cbPatentes;	
	


	public VentanaInspector(VentanaPrincipal padre, String legajo,String pass) {
		
		setTitle("Ventana Inspector "+legajo);
		ventanaPrincipal = padre;
		this.legajo = legajo;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 750, 519);
		panelPrincipal = new JPanel();
		panelPrincipal.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(panelPrincipal);		
		
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
		setResizable(false);
		
		conectarBD();	
		agregarComponentes();	
		
	}
		

	private void agregarComponentes() {
		crearPanelPatente();
		crearPanelUbicacion();
		visibilidadPanelPatentes(true);
		visibilidadPanelUbicacion(false);
					
		//Seteo listeners botones
		setListeners();		
	}
	
		

	
	//---------------------------------------//
	//------------Oyentes------------------//
	//---------------------------------------//	

	private void setListeners() {		

		agregarPatente.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (patenteNumeros.getText().trim().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Debe ingresar el numero de la patente!");
				} else if (patenteLetras.getText().trim().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Debe ingresar las letras de la patente!");
				} else {
					String patente = patenteLetras.getText()+""+patenteNumeros.getText();
					if(verificarPatente(patente)){
						boolean esta = false;
						//verifico que no la haya agregado
						for(int i=0; i<patentes.size() && !esta; i++)
							esta = (patentes.get(i).equals(patente));
						
						//si no esta la agrego
						if(!esta){
							patentes.add(patente);
							mensaje.setText("Patente "+patente+" agregada");
						}else{
							mensaje.setText("La pantente ya fue agregada");						
						}
						//reseteo lugares para agregar otra
						patenteLetras.setText("");
						patenteNumeros.setText("");
						cbPatentes.addItem(patente);
					}else{
						mensaje.setText("La patente "+patente+" no está registrada");
						patenteLetras.setText("");
						patenteNumeros.setText("");
					}	
				}
				
			
				patenteLetras.setValue(null);
				patenteNumeros.setValue(null);
				
			}
		});

		conectarParquimetro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				patenteLetras.setValue(null);
				patenteNumeros.setValue(null);	
				visibilidadPanelPatentes(false);
				visibilidadPanelUbicacion(true);				
			}
		});

		cbCalle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				visibilidadPanelPatentes(false);
				calle = cbCalle.getItemAt(cbCalle.getSelectedIndex());
				cbAltura.removeAllItems();
				agregarOpcionesAltura();								
			}
		});
		
		cbAltura.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				altura = cbAltura.getItemAt(cbAltura.getSelectedIndex());
				cbParquimetro.removeAllItems();
				agregarOpcionesParquimetros();								
			}
		});
		

		btnGenerarMultas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parquimetro = cbParquimetro.getItemAt(cbParquimetro.getSelectedIndex());
				if(verificarUbicacionInspector(legajo,calle,altura)){
					visibilidadPanelUbicacion(false);
					registrarAccesoParquimetro(legajo, parquimetro);
					registrarMultas();
					crearPanelMultas();
					
				}else{
					JOptionPane.showMessageDialog(null, "No tiene permisos para acceder al parquimetro en este día y horario");
				}				
			}
		});
		

		btnEliminarPatente.setFont(new Font("Calibri", Font.BOLD, 12));
		btnEliminarPatente.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					//patente a eliminar
					String elegido = cbPatentes.getSelectedItem().toString();
					patentes.remove(elegido);
					cbPatentes.removeItem(elegido);					
					JOptionPane.showMessageDialog(null, "Patente eliminada.");
					
				}catch(NullPointerException e){
					JOptionPane.showMessageDialog(null, "Seleccione una patente.");						
				}
				
			}
		});
		
		btnVolver.setFont(new Font("Calibri", Font.BOLD, 12));
		btnVolver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cerrarBD();
				dispose();
				ventanaPrincipal.restaurar();
			}
		});
		
	}	
	
	
	//---------------------------------------//
	//------------INSERTAR------------------//
	//---------------------------------------//

	//inserta una multa con la pantente dada
	private void crearMulta(String patente){
		String consulta = "INSERT INTO multa (fecha, hora, patente, id_asociado_con) VALUES (now(),now(),'"+patente+"','"+id_asociado_con+"');";
		try{
			Statement s= (Statement) conexion.createStatement();
			s.executeUpdate(consulta);	
			
			ArrayList<String> multa = new ArrayList<String>();
			multa.add(patente);
			multa.add(id_asociado_con);
			multas.add(multa);
			
		}catch (SQLException ex){
	         // en caso de error, se muestra la causa en la consola
	         System.out.println("SQLException: " + ex.getMessage());
	         System.out.println("SQLState: " + ex.getSQLState());
	         System.out.println("VendorError: " + ex.getErrorCode());
	         JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),ex.getMessage() + "\n","Error al Querer acceder.",JOptionPane.ERROR_MESSAGE);
	      }
	}
	
	//registra un acceso del inspector al parquimetro
	private void registrarAccesoParquimetro(String legajo, String parquimetro){
		
		String sentencia = "INSERT INTO accede (legajo,id_parq,fecha,hora) VALUES ('"+legajo+"','"+parquimetro+"',now(),now());";
		try{
			Statement s=(Statement) conexion.createStatement();
			s.executeUpdate(sentencia);			
			
		}catch (SQLException ex){
	         // en caso de error, se muestra la causa en la consola
	         System.out.println("SQLException: " + ex.getMessage());
	         System.out.println("SQLState: " + ex.getSQLState());
	         System.out.println("VendorError: " + ex.getErrorCode());
	         JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),ex.getMessage() + "\n","Error al Querer acceder.",
	                                       JOptionPane.ERROR_MESSAGE);
	         
	      }
	}
	
	
	//---------------------------------------//
	//------------CONSULTAS------------------//
	//---------------------------------------//
	
	private String getDayOfWeek(){
		
		Calendar calendar = Calendar.getInstance();
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		String dia = null;		
		if(dayOfWeek==Calendar.SUNDAY){
			dia = "Do";
		}else if(dayOfWeek==Calendar.MONDAY){
			dia = "Lu";
		}else if(dayOfWeek==Calendar.TUESDAY){
			dia = "Ma";
		}else if(dayOfWeek==Calendar.WEDNESDAY){
			dia = "Mi";
		}else if(dayOfWeek==Calendar.THURSDAY){
			dia = "Ju";
		}else if(dayOfWeek==Calendar.FRIDAY){
			dia = "Vi";
		}else if(dayOfWeek==Calendar.SATURDAY){
			dia = "Sa";
		}
		
		return dia;
	} 
	
	private String getTurno(){
		//calculamos el turno actual
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat formato = new SimpleDateFormat("HH");
        int hora = Integer.parseInt(formato.format(cal.getTime()));        
        String turno = null;
        if(hora>=8 && hora<=13){
        	turno = "M";
        }else if(hora>=14 && hora<=20){
        	turno = "T";
        }
        return turno;
	}
	
	
	//Verifica que el inspector tenga permiso para regiStrar multas en esa ubicacion
	private boolean verificarUbicacionInspector(String legajo, String calle, String altura){		
        //calculamos dia
		String dia = getDayOfWeek();
		String turno = getTurno();
		
		//si alguno no es valido, es falso
		if(dia==null || turno==null)
			return false;
		
		String consulta="select id_asociado_con from asociado_con where calle='"+calle+"' and altura='"+altura+"' and legajo='"+legajo+"' and dia='"+dia+"' and turno='"+turno+"';";
		boolean respuesta=false;
		try{
			Statement s=(Statement) conexion.createStatement();
			s.executeQuery(consulta);
			ResultSet rs= s.getResultSet();			
			rs = s.getResultSet();
			while (rs.next()) {
				id_asociado_con = rs.getString(1).toString();
				respuesta = true;
			}	
	       		
			
		}catch (SQLException ex){
	         // en caso de error, se muestra la causa en la consola
	         System.out.println("SQLException: " + ex.getMessage());
	         System.out.println("SQLState: " + ex.getSQLState());
	         System.out.println("VendorError: " + ex.getErrorCode());
	         JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),ex.getMessage() + "\n","Error al Querer acceder.",JOptionPane.ERROR_MESSAGE);
	 
	    }
		return respuesta;
	}
	
	//verifica que patentes hay que guardar para hacerles una multa
	private void registrarMultas(){
		String consulta = "SELECT patente FROM estacionados WHERE calle='"+calle+"' and altura='"+altura+"';";
		multas = new ArrayList<ArrayList<String>>();
		
		try{
			Statement s= (Statement) conexion.createStatement();
			s.executeQuery(consulta);
			ResultSet rs = s.getResultSet();
			while (rs.next()) {
				String patente = rs.getString(1).toString();
				patentes.remove(patente);
			}	
			
			for(String patente: patentes){
				crearMulta(patente);
			}		
			
			crearPanelMultas();
			
		}catch (SQLException ex) {
	         // en caso de error, se muestra la causa en la consola
	         System.out.println("SQLException: " + ex.getMessage());
	         System.out.println("SQLState: " + ex.getSQLState());
	         System.out.println("VendorError: " + ex.getErrorCode());
	         JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),ex.getMessage() + "\n", "Error al Querer acceder.",JOptionPane.ERROR_MESSAGE);
	    }
	}
	

	//verifica que la patente esté registrada en la base de datos
	private boolean verificarPatente(String patente){
		String consulta="select patente from automoviles where patente='"+patente+"';";
		boolean respuesta=false;
		try{
			Statement s=(Statement) conexion.createStatement();
			s.executeQuery(consulta);
			ResultSet rs= s.getResultSet();
			respuesta=rs.next();			
			
		}catch (SQLException ex){
	         // en caso de error, se muestra la causa en la consola
	         System.out.println("SQLException: " + ex.getMessage());
	         System.out.println("SQLState: " + ex.getSQLState());
	         System.out.println("VendorError: " + ex.getErrorCode());
	         JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),ex.getMessage() + "\n","Error al Querer acceder.",JOptionPane.ERROR_MESSAGE);
	    }
		return respuesta;
	}

	//agrega las opciones de calle que hay en el sistema 
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
	
	//---------------------------------------//
	//------------PANELES GRAFICOS-----------//
	//---------------------------------------//
	

	
	private void crearPanelPatente(){
		patentes = new ArrayList<String>();
		
		patenteLetras = new JFormattedTextField(createFormatter("UUU"));  //con el createFormater nos aseguramos que sean 3 letras
		//patenteLetras.setToolTipText("AAA");		
		patenteNumeros = new JFormattedTextField(createFormatter("###")); //con el createFormater nos aseguramos que sean 3 digitos
		//patenteNumeros.setToolTipText("111");
		
		titulo = new JLabel("Insertar Patente");
		titulo.setFont(new Font("Tahoma", Font.BOLD, 20));
		
		agregarPatente = new JButton("Agregar Patente");
		agregarPatente.setFocusPainted(false);

		
		conectarParquimetro = new JButton("Conectar a Parquimetro");
		conectarParquimetro.setFocusPainted(false);

		
		btnVolver = new JButton("Volver");
		btnVolver.setFocusPainted(false);
		
		mensaje = new JLabel(" ");
		mensaje.setHorizontalAlignment(SwingConstants.LEFT);
		mensaje.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		tablaMultas.setEditable(false);
		
		
		titulo2 = new JLabel("Eliminar Patente");
		titulo2.setFont(new Font("Tahoma", Font.BOLD, 20));
		btnEliminarPatente = new JButton("Eliminar");			
		
		cbPatentes = new JComboBox<String>();
		
		
		panelPatente = new GroupLayout(panelPrincipal);
		panelPatente.setHorizontalGroup(
			panelPatente.createParallelGroup(Alignment.TRAILING)
				.addGroup(panelPatente.createSequentialGroup()
					.addGap(24)
					.addComponent(btnVolver, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(640, Short.MAX_VALUE))
				.addGroup(panelPatente.createSequentialGroup()
					.addGap(150)
					.addGroup(panelPatente.createParallelGroup(Alignment.TRAILING)
						.addComponent(mensaje, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
						.addGroup(panelPatente.createSequentialGroup()
							.addGroup(panelPatente.createParallelGroup(Alignment.LEADING)
								.addComponent(agregarPatente, GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
								.addGroup(panelPatente.createParallelGroup(Alignment.LEADING, false)
									.addComponent(titulo, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addGroup(panelPatente.createSequentialGroup()
										.addComponent(patenteLetras, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(patenteNumeros, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)))
								.addComponent(conectarParquimetro, GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE))
							.addGap(68)
							.addGroup(panelPatente.createParallelGroup(Alignment.TRAILING)
								.addComponent(cbPatentes, Alignment.LEADING, 0, 168, Short.MAX_VALUE)
								.addComponent(titulo2, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnEliminarPatente, GroupLayout.PREFERRED_SIZE, 168, GroupLayout.PREFERRED_SIZE))))
					.addGap(180))
		);
		panelPatente.setVerticalGroup(
			panelPatente.createParallelGroup(Alignment.TRAILING)
				.addGroup(panelPatente.createSequentialGroup()
					.addContainerGap(126, Short.MAX_VALUE)
					.addGroup(panelPatente.createParallelGroup(Alignment.TRAILING)
						.addComponent(titulo)
						.addComponent(titulo2, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addGap(45)
					.addGroup(panelPatente.createParallelGroup(Alignment.LEADING)
						.addGroup(panelPatente.createSequentialGroup()
							.addComponent(cbPatentes, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
							.addGap(22))
						.addGroup(Alignment.TRAILING, panelPatente.createSequentialGroup()
							.addGroup(panelPatente.createParallelGroup(Alignment.BASELINE)
								.addComponent(patenteLetras, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
								.addComponent(patenteNumeros, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE))
							.addGap(18)))
					.addGap(18)
					.addComponent(mensaje, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
					.addGap(14)
					.addGroup(panelPatente.createParallelGroup(Alignment.LEADING)
						.addGroup(panelPatente.createSequentialGroup()
							.addComponent(btnEliminarPatente, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(btnVolver, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
						.addGroup(Alignment.TRAILING, panelPatente.createSequentialGroup()
							.addComponent(agregarPatente, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
							.addGap(17)
							.addComponent(conectarParquimetro, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
							.addGap(98))))
		);
		panelPrincipal.setLayout(panelPatente);
	}
	
	private void crearPanelUbicacion(){
		
		cbCalle = new JComboBox<String>();	
		agregarOpcionesCalle();
		lblCalle = new JLabel("Calle");
		lblCalle.setFont(new Font("Tahoma", Font.BOLD, 15));		
		cbAltura = new JComboBox<String>();
		cbAltura.setEditable(false);		
		lblAltura = new JLabel("Altura");
		lblAltura.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnGenerarMultas = new JButton("Generar Multas");		
		cbParquimetro = new JComboBox<String>();	
		cbParquimetro.setEditable(false);
		lblParquimetro = new JLabel("Parquimetro");
		lblParquimetro.setFont(new Font("Tahoma", Font.BOLD, 15));		
		panelUbicacion = new GroupLayout(panelPrincipal);
		panelUbicacion.setHorizontalGroup(
			panelUbicacion.createParallelGroup(Alignment.TRAILING)
				.addGroup(panelUbicacion.createSequentialGroup()
					.addContainerGap(214, Short.MAX_VALUE)
					.addGroup(panelUbicacion.createParallelGroup(Alignment.TRAILING)
						.addGroup(panelUbicacion.createSequentialGroup()
							.addComponent(lblParquimetro, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(cbParquimetro, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE))
						.addGroup(panelUbicacion.createParallelGroup(Alignment.TRAILING, false)
							.addGroup(panelUbicacion.createSequentialGroup()
								.addComponent(lblAltura, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGap(28)
								.addComponent(cbAltura, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE))
							.addGroup(panelUbicacion.createSequentialGroup()
								.addComponent(lblCalle, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGap(63)
								.addComponent(cbCalle, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE))))
					.addGap(225))
				.addGroup(Alignment.LEADING, panelUbicacion.createSequentialGroup()
					.addGap(301)
					.addComponent(btnGenerarMultas, GroupLayout.PREFERRED_SIZE, 154, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(295, Short.MAX_VALUE))
		);
		panelUbicacion.setVerticalGroup(
			panelUbicacion.createParallelGroup(Alignment.LEADING)
				.addGroup(panelUbicacion.createSequentialGroup()
					.addGap(136)
					.addGroup(panelUbicacion.createParallelGroup(Alignment.BASELINE)
						.addComponent(cbCalle, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblCalle))
					.addGap(44)
					.addGroup(panelUbicacion.createParallelGroup(Alignment.LEADING)
						.addComponent(cbAltura, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
						.addGroup(panelUbicacion.createSequentialGroup()
							.addGap(6)
							.addComponent(lblAltura, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)))
					.addGap(42)
					.addGroup(panelUbicacion.createParallelGroup(Alignment.LEADING)
						.addComponent(cbParquimetro, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
						.addGroup(panelUbicacion.createSequentialGroup()
							.addGap(6)
							.addComponent(lblParquimetro, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)))
					.addGap(54)
					.addComponent(btnGenerarMultas, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(58, Short.MAX_VALUE))
		);
		panelPrincipal.setLayout(panelUbicacion);		
		
	}
	
	
	private void crearPanelMultas(){
		
		JLabel titulo = new JLabel("Multas Generadas");
		titulo.setFont(new Font("Tahoma", Font.PLAIN, 27));
		panelMultas = new GroupLayout(panelPrincipal);
		panelMultas.setHorizontalGroup(
			panelMultas.createParallelGroup(Alignment.LEADING)
				.addGroup(panelMultas.createSequentialGroup()
					.addGroup(panelMultas.createParallelGroup(Alignment.LEADING)
						.addGroup(panelMultas.createSequentialGroup()
							.addGap(141) 
							.addComponent(tablaMultas, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(panelMultas.createSequentialGroup()
							.addGap(247) 
							.addComponent(titulo, GroupLayout.PREFERRED_SIZE, 255, GroupLayout.PREFERRED_SIZE))) 
					.addContainerGap(141, Short.MAX_VALUE))
		);
		panelMultas.setVerticalGroup(
			panelMultas.createParallelGroup(Alignment.LEADING)
				.addGroup(panelMultas.createSequentialGroup()
					.addGap(84) 
					.addComponent(titulo, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
					.addGap(60) 
					.addComponent(tablaMultas, GroupLayout.PREFERRED_SIZE, 255, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(48, Short.MAX_VALUE)) 
		);
		panelPrincipal.setLayout(panelMultas);	
		
		
		imprimirMultas();
		
	}
	
	private void imprimirMultas(){
		String consulta = "SELECT numero,fecha,hora,calle,altura,patente,legajo FROM multa NATURAL JOIN asociado_con WHERE id_asociado_con='"+multas.get(0).get(1)+"' ORDER BY numero DESC LIMIT "+multas.size()+"";
				
		try
	      {  
			Statement s=(Statement) conexion.createStatement();
			if(s.execute(consulta)){
	    	  tablaMultas.setSelectSql(consulta.trim()); //Se intenta hacer la consulta
	    	  tablaMultas.createColumnModelFromQuery();  
	    	  for (int i = 0; i < tablaMultas.getColumnCount(); i++)
	    	  { // Se ajusta el formato de la hora 		   		  
	    		 if	 (tablaMultas.getColumn(i).getType()==Types.TIME)  
	    		 {    		 
	    		  tablaMultas.getColumn(i).setType(Types.CHAR);  
	  	       	 }
	    		 // Se ajusta el formato de la fechas
	    		 if	 (tablaMultas.getColumn(i).getType()==Types.DATE)
	    		 {
	    		    tablaMultas.getColumn(i).setDateFormat("dd/MM/YYYY");
	    		 }
	          }   	     	
	    	  tablaMultas.refresh();
	       }
			
	      }catch (SQLException ex)
		      {
		         System.out.println("SQLExcepcion: " + ex.getMessage());
		         System.out.println("SQLEstado: " + ex.getSQLState());
		         System.out.println("CodigoError: " + ex.getErrorCode());
		         JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
		                                       ex.getMessage() + "\n", 
		                                       "Error al ejecutar la consulta.",
		                                       JOptionPane.ERROR_MESSAGE);
		         
		      }
		
	}
	

	private void visibilidadPanelPatentes(boolean bool){
		patenteLetras.setVisible(bool);
		patenteNumeros.setVisible(bool);
		agregarPatente.setVisible(bool);
		conectarParquimetro.setVisible(bool);	
		titulo.setVisible(bool);
		mensaje.setVisible(bool);
		titulo2.setVisible(bool);
		cbPatentes.setVisible(bool);
		btnEliminarPatente.setVisible(bool);
		
		if(bool){
			panelPrincipal.setLayout(panelPatente);
		}
		
	}
	private void visibilidadPanelUbicacion(boolean bool){
		lblCalle.setVisible(bool);
		lblAltura.setVisible(bool);
		lblParquimetro.setVisible(bool);
		cbCalle.setVisible(bool);
		cbAltura.setVisible(bool);
		cbParquimetro.setVisible(bool);
		btnGenerarMultas.setVisible(bool);
		
		if(bool){
			panelPrincipal.setLayout(panelUbicacion);
		}
	}
	

	
	//---------------------------------------//
	//------------CONEXIONES A BASE----------//
	//---------------------------------------//
	
	//conecta a la base cn permisos de inspector
	private void conectarBD() {
		try {
			String driver = "com.mysql.jdbc.Driver";
			String servidor = "localhost:3306";
			String baseDatos = "parquimetros";
			String url = "jdbc:mysql://" + servidor + "/" + baseDatos;
			
			//tablaMultas= new DBTable();
			tablaMultas.connectDatabase(driver, url,"inspector", "inspector");
			conexion = DriverManager.getConnection(url, "inspector", "inspector");
	
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this,"Se produjo un error al intentar conectarse a la base de datos.\n" + ex.getMessage(), "Error",JOptionPane.ERROR_MESSAGE);
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}	
	
	//cierra la base de datos
	private void cerrarBD(){
		try{
		     if (this.conexion != null){
		        conexion.close();
		        conexion = null;
		     } 
		     tablaMultas.close();
		 }catch (SQLException ex){
			 System.out.println("SQLExcepcion " + ex.getMessage());
			 System.out.println("SQLEstado: " + ex.getSQLState());
			 System.out.println("CodigoError: " + ex.getErrorCode());
		 }	    
	}
	
	//Metodo para crear mascara de los campos letras y numeros de patente.
    protected MaskFormatter createFormatter(String s) {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter(s);
        } catch (java.text.ParseException exc) {
            System.err.println("formatter is bad: " + exc.getMessage());
            System.exit(-1);
        }
        return formatter;
    }
}