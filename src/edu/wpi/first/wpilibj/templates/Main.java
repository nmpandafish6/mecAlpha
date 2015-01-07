/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.templates;


import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Main extends IterativeRobot {
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    
    CANJaguar frontLeft;
    CANJaguar frontRight;
    CANJaguar backRight;
    CANJaguar backLeft;
    Joystick joy;
    Joystick joy2;
    Gyro gyro;
    double gyroAngle = 0;
    
    public void robotInit() {
        try {
            frontLeft = new CANJaguar(3);
            frontRight = new CANJaguar(4);
            backRight = new CANJaguar(5);
            backLeft = new CANJaguar(2);
            joy = new Joystick(1);
            joy2 = new Joystick(2);
            gyro = new Gyro(2);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        gyroAngle = gyro.getAngle();
        SmartDashboard.putNumber("Gyro Angle", gyroAngle);
        
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
//        double forward = joy.getX();
//        double strafe = joy.getY();
//        double rotate = joy2.getX()/2;
        
        gyroAngle = gyro.getAngle();
        double gyroAngleRads = gyroAngle * Math.PI / 180;
        double desiredAngle = (MathUtils.atan2(joy.getY(), joy.getX())+3/2*Math.PI) % Math.PI;
        double relativeAngle = -(gyroAngleRads) + (desiredAngle) + 90;
        double forward = Math.sin(relativeAngle);
        double strafe = Math.cos(relativeAngle);
        double rotate = joy.getX();
        double scalar = Math.abs(joy.getMagnitude());
        
        
        double ftLeft = (forward + strafe)*scalar + rotate;
        double ftRight = (-forward + strafe)*scalar + rotate;
        double bkLeft = (forward - strafe)*scalar + rotate;
        double bkRight = (-forward - strafe)*scalar + rotate;
        
        double output[] = normalize(ftLeft, ftRight, bkLeft, bkRight);
        
        ftLeft = output[0];
        ftRight = output[1];
        bkLeft = output[2];
        bkRight = output[3];
        
        try{
            frontLeft.set(ftLeft);
            frontRight.set(ftRight);
            backLeft.set(bkLeft);
            backRight.set(bkRight);
        }catch(Exception e){
            System.out.println("Hey Listen");
        }
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    
    }
    
    public double[] normalize(double value1, double value2, double value3, double value4){
        double[] normalizedValues = new double[4];
        double max = Math.max(Math.abs(value1), Math.abs(value2));
        max = Math.max(Math.abs(value2), max);
        max = Math.max(Math.abs(value3), max);
        max = Math.max(Math.abs(value4), max);
        
        normalizedValues[0] = value1 / max;
        normalizedValues[1] = value2 / max;
        normalizedValues[2] = value3 / max;
        normalizedValues[3] = value4 / max;
        
        
        return normalizedValues;
    }
    
}