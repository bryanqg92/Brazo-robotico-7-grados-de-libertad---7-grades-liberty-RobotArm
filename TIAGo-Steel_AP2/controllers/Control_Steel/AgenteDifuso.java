
/**
 *
 * @author Dante Sterpin
 */
public class AgenteDifuso
{
    private double vA[],vmA[],numerador,denominador,salida;
    private double present_error[],deri_error[],error[];
    private double past_error[] = new double[7];
    private double fuzzy_error[],fuzzy_de[],relacion_AND[][];
    
    private double error_min[]= {-2.61,-2.52,-4.96,-2.61,-4.14,-2.78,-4.41};
    private double vel_max[]= {1.95, 1.95, 2.35, 2.35, 1.95, 1.76, 1.76};
    
    private double [][]cons_difusa_error,cons_difusa_de,centros,aux_defuzzyficacion;
      		
    
    public AgenteDifuso()
    {
        vA = new double[7];
        present_error = new double[7];
        deri_error = new double[7];
        //past_error = new double[7];
        fuzzy_error = new double[7];
        fuzzy_de = new double[5];
        relacion_AND = new double[5][7];
        aux_defuzzyficacion = new double[5][7];
        cons_difusa_error = new double[][] 
		{	
                                  {-102, -101, -50, -45},		
			{-50, -45, -45, -25},		
			{-45, -25, -15, 0},	
			{-15, 0, 0, 15},	
			{0, 15, 25, 45},	
			{25, 45, 45, 50},
			{45, 50, 101, 102}
								
  		};
        cons_difusa_de = new double[][]
		{	
                                  {-202, -201, -160, -100},		
			{-160, -100, -60, 0},		
			{-60, 0, 0, 60},	
			{0, 60, 100, 160},	
			{100, 100, 201, 202},	
								
  		};
        centros = new double [][]
  		{	
                          {0, 0, 0, 0, 0, 0, 0},		
                          {-70, -50, -30, -15, 0, 0, 90},		
                          {-90, -70, -50, 0, 50, 70, 90},		
                          {-90, 0, 0, 15, 30, 50, 70},		
                          {0, 0, 0, 0, 0, 0, 0}							
  		};
  		//System.out.print(centros.length+" "+centros[0].length);
        
            }
    
    public double[] DecisionMaking(double[] dA,double[] sA)
    {
      for(int x=0; x<7; x++)
       {           
         double error = dA[x]-sA[x];
         present_error[x]=Lin_error(error, error_min[x], error_min[x]*-1,-100,100);
         

         deri_error[x]=past_error[x]-present_error[x];
         //System.out.println(present_error[x]+" " + deri_error[x]+" "+ past_error[x]);
         past_error[x]=present_error[x];
         fuzzy_error(present_error[x]);
         fuzzy_der_error(deri_error[x]);
         razonamiento_difuso();
         defuzzyficacion();
         salida=(numerador/denominador);
         vA[x]=Lin_error(salida, -100, 100, vel_max[x]*-1, vel_max[x]);
         
         

       }
      /*
        vA[0] = 0;  // Hombro lateral
        vA[1] = 0;  // Hombro vertical
        vA[2] = 0;  // Brazo superior
        vA[3] = 0;  // Codo del Brazo
        vA[4] = 0;  // Brazo inferior
        vA[5] = 0;  // Muñeca del Brazo
        vA[6] = 0;  // Rotor de Muñeca*/
        
        return vA;
    }
    
    public double Lin_error(double error, double oldmin, double oldmax, double newmin, double newmax)
    {
      double mean= ((((error-(oldmin))*(newmax-(newmin)))/(oldmax-(oldmin)))+(newmin));             
      return mean;
    }
    
    public void fuzzy_error(double in_error)
    {
      for(int fila_cons=0; fila_cons<cons_difusa_error.length; fila_cons++)
      {
          if(in_error<=cons_difusa_error[fila_cons][0]) 
          {
              fuzzy_error[fila_cons]=0;
          }
	
          else if(cons_difusa_error[fila_cons][0] < in_error && in_error < cons_difusa_error[fila_cons][1] ) 
          {
              fuzzy_error[fila_cons]=(in_error-cons_difusa_error[fila_cons][0])/(cons_difusa_error[fila_cons][1]-cons_difusa_error[fila_cons][0]);

            }
          else if(cons_difusa_error[fila_cons][1] <= in_error && in_error <= cons_difusa_error[fila_cons][2]) 
           {
              fuzzy_error[fila_cons]=1;
            }
          else if(cons_difusa_error[fila_cons][2] < in_error && in_error < cons_difusa_error[fila_cons][3] ) 
          {
              fuzzy_error[fila_cons]=(cons_difusa_error[fila_cons][3]-in_error)/(cons_difusa_error[fila_cons][3]-cons_difusa_error[fila_cons][2]);
          }
          else if(in_error >= cons_difusa_error[fila_cons][3]) 
          {
              fuzzy_error[fila_cons]=0;
          }
      }
     }
     
    public void fuzzy_der_error(double in_de_error)
    {
      for(int fila_cons=0; fila_cons<5; fila_cons++)
      {
          if(in_de_error<=cons_difusa_de[fila_cons][0]) 
          {
              fuzzy_de[fila_cons]=0;
          }
          else if(cons_difusa_de[fila_cons][0] < in_de_error && in_de_error < cons_difusa_de[fila_cons][1] ) 
          {
              fuzzy_de[fila_cons]=(in_de_error-cons_difusa_de[fila_cons][0])/(cons_difusa_de[fila_cons][1]-cons_difusa_de[fila_cons][0]);
            }
          else if(cons_difusa_de[fila_cons][1] <= in_de_error && in_de_error <= cons_difusa_de[fila_cons][2]) 
           {
              fuzzy_de[fila_cons]=1;
            }
          else if(cons_difusa_de[fila_cons][2] < in_de_error && in_de_error < cons_difusa_de[fila_cons][3] ) 
          {
              fuzzy_de[fila_cons]=(cons_difusa_de[fila_cons][3]-in_de_error)/(cons_difusa_de[fila_cons][3]-cons_difusa_de[fila_cons][2]);
          }
          else if(in_de_error >= cons_difusa_de[fila_cons][3]) 
          {
              fuzzy_de[fila_cons]=0;
          }
      }
     }
    
    public void razonamiento_difuso()
    {
    	for(int fil=0; fil<5; fil++)
    	{
    		for(int col=0; col<fuzzy_error.length; col++)
    		{
    			relacion_AND[fil][col]=Math.min(fuzzy_error[col], fuzzy_de[fil]);
    		}
    	}
    
    
    }
    
    public void defuzzyficacion()
    {
    	for(int fil=0; fil < 5; fil++) 
    	{
    		for(int col=0; col < 7; col++) 
    		{			   
    			aux_defuzzyficacion[fil][col] = relacion_AND[fil][col]  * centros[fil][col];
    		}
    	}
    	
        numerador=0;
        salida=0;
        denominador=0;

        for(int fila2=0; fila2<5; fila2++) 
        {
        	for(int col2=0; col2<7; col2++) 
        	{			   
        		denominador = denominador + relacion_AND[fila2][col2];
        		numerador = numerador + aux_defuzzyficacion[fila2][col2];	    				     
        	}	   
        }
    
    }


}