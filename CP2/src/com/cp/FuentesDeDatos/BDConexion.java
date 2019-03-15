package com.cp.FuentesDeDatos;

/*
 * Esta clase sirve para trabajar con POOL DE CONEXIONES.
 ************************************************************************
 *	CUIDADO !  SI SE REDEPLOYA...CASCAAAA !!!! (Hay de REINICIAR el server; no basta tampoco con 'Undeploy' + 'Deploy')
 *       (Mira a ver si se esta reutilizando algún objeto de la "implementación" que se undeploya antes de su nuevo "deploy")
 *
 *	CUIDADO !  SI SE MATAN LOS TRABAJOS EN EL 400...CASCAAAA !!!!
 *		   Hay de REINICIAR el server o 
 *		   Si se marca la sgte,opción en el server en "Recursos > JDBC > Conjuntos de conexiones", 
 *                   se regeneran las conexiones automáticamente:
 *	   Connection Validation:   X Required (Validate connections, allow server to reconnect in case of failure)
 *                                    
 *
 *  Id. del mensaje  ApplicationDispatcher[/ModeloVacio_V66] PWC1231
    Mensaje completo:
	Servlet.service() para el servlet action desencadenó una excepción java.lang.NullPointerException
	    at com.ibm.as400.access.JDProperties.getIndex(JDProperties.java:1490)
	    at com.ibm.as400.access.SQLConversionSettings.<init>(SQLConversionSettings.java:80)
	    at com.ibm.as400.access.AS400JDBCStatement.<init>(AS400JDBCStatement.java:188)
	    at com.ibm.as400.access.AS400JDBCConnection.createStatement(AS400JDBCConnection.java:707)
	    at com.ibm.as400.access.AS400JDBCConnection.createStatement(AS400JDBCConnection.java:651)
	    at com.sun.gjc.spi.base.ConnectionHolder.createStatement(ConnectionHolder.java:258)
	    at com.billin.FuentesDeDatos.BDPoolConexion.executeQuery(BDPoolConexion.java:219)
	    at com.billin.qa_GruposClaves.db.QaAccesoBaseDatos.qa_getRcd(QaAccesoBaseDatos.java:105)
	    at com.billin.qa_GruposClaves.actions.QaEDTRCD_A.cargarPantalla(QaEDTRCD_A.java:112)
	    at com.billin.qa_GruposClaves.actions.QaEDTRCD_A.execute(QaEDTRCD_A.java:65)
	    at  .....
 ************************************************************************
 * POSTGRES - Configurar dos cosas en el server:
 * 	1 . Resources / JDBC / JDBC Connection Pools: CREAR NUEVO
 * 			Pool name:				agrupado
 * 			Resource type:			javax.sql.DataSource
 * 			Database Driver Vendor:	Postgresql
 * 
 * 			Datasource Classname:	org.postgresql.ds.PGSimpleDatasource
 * 
 * 			Aditional properties:
 * 					portNumber		5432
 * 					databaseName	Billin_V30
 * 					datasourceName	Billin
 * 					serverName		127.0.0.1
 * 					user			postgres
 * 					password		adminadmin
 * 	
 * 	2 . Resources / JDBC / JDBC Resources: CREAR NUEVO
 * 			JNDI Name:				jdbc/agrupado
 * 			Pool Name:				agrupado 
 ************************************************************************
 *
 *	1 . Debe haberse configurado un POOL en el SERVER en el que se despliegue la aplicación.
 *		P.Ej. Para AS400, instalar en el GlassFish la lib 'jt400.jar' y crear:
 *                    un 'POOL'(Conjuntos de conexiones) en "Recursos > JDBC > Conjuntos de conexiones":
 *			    Nombre JNDI:                            AS400
 *			    Tipo de recurso:                        javax.sql.DataSource
 *			    Nombre de clase de la fuente de datos:  com.ibm.as400.access.AS400JDBCDataSource
 *		            Mas las propiedades:
 *                                  serverName 181.75.11.11
 *                                  user       desa2
 *                                  password   desa2
 *                    un 'RECURSO JDBC ASOCIADO' "en Recursos > JDBC > Recursos JDBC":
 *			    Nombre JNDI:                            jdbc/agrupado
 *			    Conjunto de conexiones:                 AS400
 *
 *	2 . En la constante "nombrePool" de esta clase se debe meter el nombre del pool declarado en el Server. (p.ej."java:comp/env/jdbc/agrupado")
 *	3 . Las funciones del DAO deben alterarse de la sgte.manera:
 *		a . El parámetro 'BDConexion bd' no se usará dentro de las funciones DAO.
 *		b . TODAS las funciones DAO se se modificarán según lo descrito a continuación.
 *	    (En general, antes de cada contacto con la BD se 'conectará' y tras cada acción, SIEMPRE se 'desconectará')
 *
 *	4 . En el archivo del proyecto 'web.xml'debe declararse la referencia al JDBC.
      <resource-ref>
	<description>Para usar pool JDBC.</description>
	<res-ref-name>jdbc/agrupado</res-ref-name>
	<res-type>javax.sql.DataSource</res-type>
	<res-auth>Container</res-auth>
	<res-sharing-scope>Shareable</res-sharing-scope>
      </resource-ref>
 *
 *
 *
 *
 *	    La función del DAO 'runSql' para usarse con el POOL quedará así:
 *	    (Fíjate que ya no necesita la 'database' com parámetro...)
 *
 ************************************************************************
		  private void runSql(String sql) throws StExcepcion {
		    //////////////////////////////////////////////
		    try {
			new BDPoolConexion().executeUpdate(sql);    // Encapsula apertura y devolución de conexión al POOL JDBC.
		    } catch (StExcepcion ex) {
			throw ex;
		    }
		    //////////////////////////////////////////////
		}
 ************************************************************************

 *	    Y las de lectura, (por ejemplo un 'getRcd()'), quedará alterada algo así:
 *	    (Fíjate que ya no necesita la 'database' com parámetro...)

 ************************************************************************
      public QaBean qa_getRcd(QaBean registro) throws StExcepcion {
	String sql =
		"SELECT A.*" +
		" FROM " + this.lf_RTV + " A" +
		" WHERE " +
		"  QAXOCD = '" + registro.getQa_QAXOCD() + "'" + // Grupo
		""
		;
	ResultSet rs = null;
	QaBean regRead = null;
	//////////////////////////////////////////////
>>>     BDPoolConexion dataBase = null;
	try {
>>>         dataBase = new BDPoolConexion();
>>>         dataBase.conectar();
	    java.sql.Connection poolConn; rs = dataBase.executeQuery(sql,poolConn);
	    if (rs.next()){
		regRead = new QaBean();

		regRead.setQa_QAXOCD( rs.getString("QAXOCD") ); regRead.setQa_QAXOCD( (regRead.getQa_QAXOCD() == null)?"":regRead.getQa_QAXOCD().trim() ); // Grupo
		regRead.setQa_QAZ1XT( rs.getString("QAZ1XT") ); regRead.setQa_QAZ1XT( (regRead.getQa_QAZ1XT() == null)?"":regRead.getQa_QAZ1XT().trim() ); // Nombre
	    }
	} catch (SQLException ex0) {
	    throw new StExcepcion(ex0.getMessage());
	} catch (StExcepcion ex1) {
	    throw new StExcepcion(ex1.getMessage());
	} finally {
	    try {
		if ( rs != null ) rs.close();
>>>             if ( dataBase != null ) dataBase.desConectar();
	    } catch (SQLException ex2) {
		throw new StExcepcion(ex2.getMessage());
	    }
	}
	//////////////////////////////////////////////

	return regRead;
    }

 ************************************************************************
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.NamingException;

import com.cp._comun.StExcepcion;
import com.cp._comun.Subrutinas;

public class BDConexion {
	
	private final String tag = this.getClass().getSimpleName();

	public String currentDb = "DB04"; //Valor para el fichero Config.properties

    public static boolean isTrazar = false;

//////////////////////////////////////////////////////////////////////////////////
// Para conexión CON POOL:
    public static boolean isPool = Subrutinas.m_isPool;	// FALSE si estamos con TomCat.
    public static final String POOLNAME = 
    		"jdbc/agrupado";				// Para GlassFish...
//    		"java:comp/env/jdbc/agrupado";	// Para TomCat...
//
//    Para configurar el JDBC pool en GLASSFISH:
//    ==========================================
//    	Iniciar sesión en consola de administración de Glassfish:
//    		http://localhost:4848
//    		Usr: admin
//    		Pwd: adminadmin.
//   
//    1d3 Ir a "Recursos / JDBC / Pools de Conexiones JDBC" y crear nuevo:
//    ====================================================================
//    	Nombre de Pool:		agrupado
//    	Tipo de Recurso:	javax.sql.DataSource
//    	Proveedor de Controladores de la Base de Datos:
//    						Postgresql
//    	Nombre de Clase de Origen de Datos:
//    						org.postgresql.ds.PGSimpleDataSource
//
//    	Tamaño de Pool Inicial y Mínimo: 		8
//    	Tamaño de Pool Máximo:					100
//    	Cantidad de Cambio de Tamaño del Pool:	2
//
//    	Propiedades Adicionales:
//    		portNumber		5432
//    		databaseName	Billin_V10
//    		datasourceName	Billin
//    		serverName		127.0.0.1
//    		user			postgres
//    		password		adminadmin
//
//    2d3 Ir a "Recursos JDBC" y crear nuevo:
//    =======================================
//    	Nombre JNDI:		jdbc/agrupado
//    	Nombre de Pool:		agrupado
//    
//	  3d3 Meter en fichero "web.xml" de la APLICACIÓN:
//    =======================================
//	  <resource-ref>
//	    <description>Para usar pool JDBC.</description>
//	    <res-ref-name>jdbc/agrupado</res-ref-name>
//	    <res-type>javax.sql.DataSource</res-type>
//	    <res-auth>Container</res-auth>
//	    <res-sharing-scope>Shareable</res-sharing-scope>
//	  </resource-ref>
//////////////////////////////////////////////////////////////////////////////////
//
//  Para configurar el JDBC pool en TOMCAT:
//  ==========================================
//  1. Required files. Copy the Postgres JDBC jar to $CATALINA_HOME/lib : ('postgresql-9.3-1100.jdbc4.jar')
//  2. Resource configuration. You have two choices here:
//    	2.1 Shared resource configuration (define a datasource that is shared across all Tomcat applications)
//    			NO USAR esta opción !!
//    	2.2 Application-specific resource configuration (define a datasource specifically for one application)
//    
//    			1d2 Meter en fichero "context.xml" de TOMCAT:
//    			=============================================
//    
//			    <!-- Specify a JDBC datasource -->
//			      <Resource name="jdbc/agrupado" 
//			    	auth="Container"
//			        type="javax.sql.DataSource" 
//			        username="postgres" 
//			        password="adminadmin"
//			        driverClassName="org.postgresql.ds.PGSimpleDataSource"
//			        url="jdbc:postgresql://localhost:5432/Billin_V10"
//			        maxActive="10" 
//			        maxIdle="4" />
//    
//				2d2 Meter en fichero "web.xml" de la APLICACIÓN:
//				=============================================
//
//				  <resource-ref>
//				    <description>Para usar pool JDBC.</description>
//				    <res-ref-name>jdbc/agrupado</res-ref-name>
//				    <res-type>javax.sql.DataSource</res-type>
//				    <res-auth>Container</res-auth>
//				    <res-sharing-scope>Shareable</res-sharing-scope>
//				  </resource-ref>
//				
//////////////////////////////////////////////////////////////////////////////////

    // Para conexión SIN POOL:
    public String USUARIO;
    public String PASSWORD;
    public String URL;
    public String DRIVER;
    public String OPTIONS;
// AS400 :
//    private final String DRIVER = "com.ibm.as400.access.AS400JDBCDriver";
//    private final String preIP = "jdbc:as400://";
//    private final String posIP = "";
//    private final String URL = "jdbc:as400://181.75.11.11";
//    private final String USUARIO = "DESA";
//    private final String PASSWORD = "DESA";
// SQLSERVER :
//    private final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
//    private final String preIP = "jdbc:sqlserver://";
//    private final String posIP = ":1433";
//    private final String URL = "jdbc:sqlserver://181.75.80.24:1433";
//    private final String USUARIO = "sa";
//    private final String PASSWORD = "";
// ODBC :
//    private final String DRIVER = "sun.jdbc.odbc.JdbcOdbcDriver";
//    private final String preIP = "jdbc:odbc:";
//    private final String posIP = "";
//    private final String URL = "ParaGR";
//    private final String USUARIO = "Administrador";
//    private final String PASSWORD = "";
// Oracle :
//    private final String DRIVER = "oracle.jdbc.driver.OracleDriver";
//    private final String preIP = "jdbc:oracle:thin:@";
//    private final String posIP = "";
//    private final String URL = "oradist.conversia.net";
//    private final String USUARIO = "adm_distrialia";
//    private final String PASSWORD = "distrialia";

    private Connection m_conexion;		// Objeto 'Connection' de cada instancia de clase.

    public BDConexion() throws StExcepcion {
    	
    	if ( ! isPool ) {
            this.DRIVER   = Subrutinas.getG_DB_DRIVER(currentDb);
            this.URL      = Subrutinas.getG_DB_PRE_IP(currentDb) + Subrutinas.getG_DB_IP(currentDb) + Subrutinas.getG_DB_POS_IP(currentDb);
            this.USUARIO  = Subrutinas.getG_DB_USR(currentDb);
            this.PASSWORD = Subrutinas.getG_DB_PWD(currentDb);
            this.OPTIONS  = Subrutinas.getG_DB_OPTIONS(currentDb);
            
            this.URL      = this.URL + this.OPTIONS; 
            
            if ( this.URL != null && this.USUARIO != null && this.PASSWORD !=null ) {
            	conectar();
            } else { 
            	throw new StExcepcion("Error en los datos de conexión. Verifique en el archivo '" + Subrutinas.archivo_config + "' las claves: 'configuracion.DB.DIR_IP...', etc.");
            }
//    	} else {
//    		PoolingDataSource source = new PoolingDataSource();
//    		source.setDataSourceName("A Data Source");
//    		source.setServerName("localhost");
//    		source.setDatabaseName("test");
//    		source.setUser("testuser");
//    		source.setPassword("testpassword");
//    		source.setMaxConnections(10);    	
    	}
    }

    private void checkConexion() throws StExcepcion {
        if (getConexion() == null) {
            throw new StExcepcion("Error en la conexión");
        }
    }
	//////////////////////////////////////////////////
    private void conectar() throws StExcepcion {
        if (isTrazar) System.out.println("\n\n**Conectando con la BBDD...");

        try {

        	if ( ! isPool ) {
    			// Versión conexiones directas:
                Class.forName( DRIVER );
                m_conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD );

                m_conexion.setClientInfo("ApplicationName", "em_test_application");
                
        	}
			
        } catch (SQLException e) {
        	System.err.println( "***" );
        	System.err.println( "***" );
        	System.err.println( tag + " : " + e.getCause() + "\n\t" + e.getMessage() );
        	System.err.println( "***" );
        	System.err.println( "***" );
            throw new StExcepcion("**No se puede abrir la conexión: "
                    + e.getMessage());
        } catch (ClassNotFoundException e) {
        	System.err.println( "***" );
        	System.err.println( "***" );
        	System.err.println( tag + " : " + e.getCause() + "\n\t" + e.getMessage() );
        	System.err.println( "***" );
        	System.err.println( "***" );
        	e.printStackTrace();
            throw new StExcepcion("**No se encuentra el Driver: "
                    + e.getMessage());
		}
    }

    public ResultSet executeQuery(String query) throws StExcepcion {
        if (isTrazar) System.out.println("\n**Ejecutando query: " + query );

        ResultSet rs = null;
        Connection pool_conn = null;
//        long tiempoInicio = System.currentTimeMillis();

        // 1d3 : Conseguir Connection.
        if ( isPool ) {
            try {
    			// EN CADA QUERY DEBE DEVOLVERSE AL POOL CON pool_conn.close() !!!!!
    			// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    			javax.naming.InitialContext ctx = new javax.naming.InitialContext();
    			javax.sql.DataSource ds = (javax.sql.DataSource) ctx.lookup( POOLNAME );
    			pool_conn = ds.getConnection();
    			ctx.close();
    			ctx = null;

    			pool_conn.setClientInfo("ApplicationName", "BillinApp");

    		} catch (SQLException e1) {
            	System.err.println( "***" );
            	System.err.println( "***" );
            	System.err.println( e1.getMessage() );
            	System.err.println( "***" );
            	System.err.println( "***" );
                throw new StExcepcion("**No se puede obtener la conexión: "
                        + e1.getMessage());
    		} catch (NamingException e) {
            	System.err.println( "***" );
            	System.err.println( "***" );
            	System.err.println( e.getMessage() );
            	System.err.println( "***" );
            	System.err.println( "***" );
                throw new StExcepcion("**No se encuentra el pool de conexiones: "
                        + e.getMessage());
			}
            if (isTrazar) System.out.println("...Pool Connection= " + (pool_conn==null?" *NULL* ":this.hashCode()+"."+pool_conn.hashCode()) );
        } else {
            checkConexion();
            if (isTrazar) System.out.println("...Connection= " + (m_conexion==null?" *NULL* ":this.hashCode()+"."+m_conexion.hashCode()) );
        }

        // 2d3 : Usar Connection.
        try {
        	
        	if ( isPool ) {
                Statement stmt = pool_conn.createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE
                        ,ResultSet.CONCUR_READ_ONLY
                        );
                // Otros valores para 'createStatement'...
//    			ResultSet.TYPE_SCROLL_INSENSITIVE ,
//    			ResultSet.CONCUR_UPDATABLE

                rs = stmt.executeQuery(query);

                if ( pool_conn != null && ! pool_conn.getAutoCommit() ) {
                	pool_conn.commit();
                }
//              if (isTrazar) System.out.println("Query ejecutada en (" + (System.currentTimeMillis() - tiempoInicio) + ") ms");
        	} else {
            	if ( getConexion() != null ) {
                    // Permitir mover arriba y abajo el cursor:
                    Statement stmt = getConexion().createStatement(
                            ResultSet.TYPE_SCROLL_SENSITIVE
                            ,ResultSet.CONCUR_READ_ONLY
                            );
                    // Otros valores para 'createStatement'...
//        			ResultSet.TYPE_SCROLL_INSENSITIVE ,
//        			ResultSet.CONCUR_UPDATABLE
                    
                    rs = stmt.executeQuery(query);

                    if ( getConexion() != null && ! getConexion().getAutoCommit() ) {
                    	getConexion().commit();
                    }

            	} else {
            		System.out.println("...Query no ejecutada. Connection= "+ this.hashCode() + ".NULL");
            	}
//                if (isTrazar) System.out.println("Query ejecutada en (" + (System.currentTimeMillis() - tiempoInicio) + ") ms");
        	}

        } catch (SQLException e) {
            String s =
//                    "- ErrorCode: " + e.getErrorCode() +
//                    "- SQLState : " + e.getSQLState() +
//                    "- Message : " +
                    e.getMessage(); // + "- Error en la query " + query;
            if (isTrazar) System.out.println(s);
            throw new StExcepcion(s);
        }

        // 3d3 : Liberar Connection si es Pool.
		// La liberación de la conexión se producirá en 'rsClose()'
//        if ( isPool ) {
//        	// Liberar conexión del Pool:
//        	pool_conn.close();	// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
//        }

        return rs;
    }
	public static void rsClose( BDConexion dataBase, ResultSet rs ) throws StExcepcion, SQLException {
		
		try {
			if ( rs != null && rs.getStatement() != null ) { 
				rs.getStatement().close();
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if ( isPool ) {
				rs.getStatement().getConnection().close();
//				if (dataBase.getConexion() != null ) { 
//					dataBase.getConexion().close();   // Devuelve conexión al pool.
//		            if (isTrazar) System.out.println("...Pool CLOSE Connection= " + (dataBase.getConexion()==null?" *NULL* ":dataBase.hashCode()+"."+dataBase.getConexion().hashCode()) );
//				}
			}
		}
		
	}

    public int executeUpdate(String query) throws StExcepcion {
        if (isTrazar) System.out.println("\n**Ejecutando update: " + query );
        
        int nFilasAfectadas = 0;
//        long tiempoInicio = System.currentTimeMillis();
        Statement stmt = null;
        Connection pool_conn = null;
        
        // 1d3 : Conseguir Connection.
        if ( isPool ) {
            try {
    			// EN CADA QUERY DEBE DEVOLVERSE AL POOL CON pool_conn.close() !!!!!
    			// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    			javax.naming.InitialContext ctx = new javax.naming.InitialContext();
    			javax.sql.DataSource ds = (javax.sql.DataSource) ctx.lookup( POOLNAME );
    			pool_conn = ds.getConnection();
    			ctx.close();
    			ctx = null;
    		} catch (SQLException e1) {
            	System.err.println( "***" );
            	System.err.println( "***" );
            	System.err.println( e1.getMessage() );
            	System.err.println( "***" );
            	System.err.println( "***" );
                throw new StExcepcion("**No se puede obtener la conexión: "
                        + e1.getMessage());
    		} catch (NamingException e) {
            	System.err.println( "***" );
            	System.err.println( "***" );
            	System.err.println( e.getMessage() );
            	System.err.println( "***" );
            	System.err.println( "***" );
                throw new StExcepcion("**No se encuentra el pool de conexiones: "
                        + e.getMessage());
			}
            if (isTrazar) System.out.println("...Pool Connection= " + (pool_conn==null?" *NULL* ":this.hashCode()+"."+pool_conn.hashCode()) );
        } else {
        	// No es POOL:
            checkConexion();	// Chequea la conexión 'm_conexion' del propio objeto
            if (isTrazar) System.out.println("...Connection= " + (m_conexion==null?" *NULL* ":this.hashCode()+"."+m_conexion.hashCode()) );
        }

        // 2d3 : Usar Connection.
        try {
        	
        	////////////////////////////////////////////////////////
        	////////////////////////////////////////////////////////
        	if ( isPool ) {
        		if ( pool_conn != null ) {
                    stmt = pool_conn.createStatement();
                    if ( stmt != null ) {
                        nFilasAfectadas = stmt.executeUpdate(query);
                        if ( ! pool_conn.getAutoCommit() ) {
                        	pool_conn.commit();
                        }
                    }
//                  if (isTrazar) System.out.println("Update ejecutada en (" + (System.currentTimeMillis() - tiempoInicio) + ") ms");
        		}
        	} else {
        	////////////////////////////////////////////////////////
            	if ( getConexion() != null ) {
                    stmt = getConexion().createStatement();
                    nFilasAfectadas = stmt.executeUpdate(query);
                    if ( getConexion() != null && ! getConexion().getAutoCommit() ) {
                    	getConexion().commit();
                    }
//                    if (isTrazar) System.out.println("Update ejecutada en (" + (System.currentTimeMillis() - tiempoInicio) + ") ms");
            	} else {
            		System.out.println("...update no ejecutado. Connection= "+ this.hashCode() + ".NULL");
            	}
        	}
        	////////////////////////////////////////////////////////
        	////////////////////////////////////////////////////////
        	
        } catch (SQLException e) {
            String s =
//                    "- ErrorCode: " + e.getErrorCode() +
//                    "- SQLState : " + e.getSQLState() +
//                    "- Message : " +
                    e.getMessage(); // + "- Error en la query " + query;
            if (isTrazar) System.out.println(s);
            throw new StExcepcion(s);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {}
            }
            
            // En 2015/10/03 se incluyó el paso 3d3 en el finally porque en los casques dejaba la conexión abierta al no pasar por el 3d3 !!!
            
            // 3d3 : Liberar Connection si es Pool.
            if ( isPool ) {
    			if ( pool_conn !=null ) { 
    				 try {
    					 pool_conn.close();
    					 pool_conn = null;
    				} catch (SQLException e) {
    		            String s =
//    		                    "- ErrorCode: " + e.getErrorCode() +
//    		                    "- SQLState : " + e.getSQLState() +
//    		                    "- Message : " +
    		                    e.getMessage(); // + "- Error en la query " + query;
    		            if (isTrazar) System.out.println(s);
    		            throw new StExcepcion(s);
    				}
    			}
            	// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            }
            
        }

        return nFilasAfectadas;
    }
	//////////////////////////////////////////////////
    public Connection getConexion() {
        return m_conexion;
    }
    public String getRwUpperCase() {
        return Subrutinas.getG_DB_RWUPPERCASE(currentDb);
    }
    public String getRwLike() {
        return Subrutinas.getG_DB_RWLIKE(currentDb);
    }
    public String getRwAnyString() {
        return Subrutinas.getG_DB_RWANYPATTERN(currentDb);
    }
	public String getCurrentDb() {
		return currentDb;
	}
	//////////////////////////////////////////////////

}

