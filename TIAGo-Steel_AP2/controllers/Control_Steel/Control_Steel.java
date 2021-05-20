import com.cyberbotics.webots.controller.Robot;
import com.cyberbotics.webots.controller.PositionSensor;
import com.cyberbotics.webots.controller.Motor;

/**
 *
 * @author Dante Sterpin
 */
public class Control_Steel extends Robot
{
    private final int timeStep=32;
    private PositionSensor encoderA[];
    private double sA[],dA[],vA[];
    private AgenteDifuso agente;
    private Motor arm[];
    
    public Control_Steel()
    {
        super();
        
        // Inicializa los sensores
        encoderA = new PositionSensor[7];
        for(int a=0; a<7; a++)
        {
            encoderA[a] = getPositionSensor("arm_"+(a+1)+"_joint_sensor");
            encoderA[a].enable(timeStep);
        }
        sA = new double[7];
        
        // Inicializa los motores
        arm = new Motor[7];
        for(int m=0; m<7; m++)
        {
            arm[m] = getMotor("arm_"+(m+1)+"_joint");
            arm[m].setPosition(Double.POSITIVE_INFINITY);
            arm[m].setVelocity(0.0);
        }
        dA = new double[7];
        vA = new double[7];
        
        // Inicializa los agentes
        agente = new AgenteDifuso();
    }
    
    public void Ejecutar()
    {
        while (step(64) != -1)
        {
            // Lee los sensores
            for(int a=0; a<7; a++)
            {
                sA[a] = encoderA[a].getValue();
                System.out.print(sA[a]+" ");
            }
            System.out.print("\n");
            
        dA[0] = 2.5;  // Hombro lateral
        dA[1] = -0.5;  // Hombro vertical  
        /*dA[0] = 2.5;  // Hombro lateral
        dA[1] = 1;  // Hombro vertical
        dA[2] = 1;  // Brazo superior
        dA[3] = 2;  // Codo del Brazo
        dA[4] = 1.2;  // Brazo inferior
        dA[5] = 1;  // Muñeca del Brazo
        dA[6] = 1;  // Rotor de Muñeca*/
            
            // Decide la actuación
            vA = agente.DecisionMaking(dA,sA);
            
            // Mueve los motores
            for(int m=0; m<7; m++)
            {
                arm[m].setVelocity(vA[m]);
            }
        };
        
        finalize();
    }
    
    public static void main(String[] args)
    {
        new Control_Steel().Ejecutar();
    }
}
