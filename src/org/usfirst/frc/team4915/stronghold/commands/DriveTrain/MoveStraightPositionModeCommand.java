package org.usfirst.frc.team4915.stronghold.commands.DriveTrain;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team4915.stronghold.Robot;
import org.usfirst.frc.team4915.stronghold.subsystems.DriveTrain;

import java.util.ArrayList;
import java.util.List;

public class MoveStraightPositionModeCommand extends Command {

    public static List<CANTalon> motors = Robot.driveTrain.motors;
    public double inputDistanceInches;
    private DriveTrain driveTrain = Robot.driveTrain;
    private List<Double> desiredTicksValue;
    private double driveStraightValue;
    public static double ticksToMove;
    // variables for the ticks to move equation
    private static double ticksPerCycle = 4; // number of encoder ticks per
                                             // cycle that encoder reads
    private static double gearRatio = 3; // ratio between the encoder and the
                                         // shaft
    private static double cyclesPerRotation = 256; // total cycles on encoder
                                                   // per one rotation
    private static double circumference = (14 * Math.PI); // 14 inch diameter
                                                          // wheel
    private static double gearBoxRatio = 3.2; // ration between the gear and the
                                              // shaft

    public MoveStraightPositionModeCommand(double inputDistanceInches, double speed) {

        requires(this.driveTrain);

        System.out.println("***MoveStraightPositionModeCommand inputDistance: " + inputDistanceInches + "*******");

        this.inputDistanceInches = inputDistanceInches;
        this.driveStraightValue = speed;
        System.out.println("drive straight value " + driveStraightValue);

        for (int i = 0; i < motors.size(); i++) {
            motors.get(i).setEncPosition(0);
            System.out.println("MOTOR " + i + " set to " + motors.get(i).getEncPosition());
        }
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    // Called just before this Command runs the first time

    /**
     * This initializes the variables for the distance calculator.
     */
    @Override
    public void initialize() {

        this.desiredTicksValue = new ArrayList<Double>();
        // new equation
        ticksToMove =
                ((this.inputDistanceInches * cyclesPerRotation * ticksPerCycle * gearBoxRatio * gearRatio) / (circumference));

        System.out.println("ticksToMove: " + ticksToMove);
        this.desiredTicksValue.add(ticksToMove);

    }

    /**
     * This uses the wheel circumference and the number of rotations to compute
     * the distance traveled.
     */

    // Called repeatedly when this Command is scheduled to run
    @Override
    public void execute() {
        System.out.println("Executing move straight");
        System.out.println("TICKS TO MOVE: " + ticksToMove);
        System.out.println("MOTOR 1 POSITION: " + motors.get(1).getEncPosition());
        System.out.println("MOTOR 3 POSITION: " + motors.get(3).getEncPosition());
        if (this.inputDistanceInches < 0) {
            this.driveTrain.driveStraight(this.driveStraightValue);
        } else {
            this.driveTrain.driveStraight(-this.driveStraightValue);
        }
        SmartDashboard.putNumber("Drive Straight: Input distance", this.inputDistanceInches);
    }

    @Override
    public boolean isFinished() {
        // Checking if all the motors have reached the desired tick values
        return isAverageMotorFinished();
        // return isMotorFinished(1) || isMotorFinished(3) ;
    }

    private boolean isAverageMotorFinished() {
        // averages the motors
        System.out.println("in isAverageMotorFinished");
        boolean finished = false;
        double total;
        double average;
        double desiredPosition = ticksToMove;// this.desiredTicksValue.get(1);//
                                             // all the same but we didn't want
                                             // to change everything
        // prints out the motors value
        System.out.println("Motor 1 " + motors.get(1).getEncPosition());
        System.out.println("Motor 3 " + motors.get(3).getEncPosition());
        total = Math.abs(Math.abs(motors.get(1).getEncPosition()) + Math.abs(motors.get(3).getEncPosition()));
        average = total / 2;
        System.out.println("Average of motors" + average);
        // drive backwards
        if (this.inputDistanceInches < 0) {
            finished = average <= desiredPosition;
        } else {
            finished = average >= desiredPosition;
        }
        return finished;
    }

    private boolean isMotorFinished(int i) {

        boolean finished = false;
        double desiredPosition = this.desiredTicksValue.get(0);
        System.out.println("in isMotorFinished");
        double currentPosition = Math.abs(motors.get(i).getEncPosition());
        System.out.println("Motor " + i + ": current position: " + currentPosition + ", desired position " + desiredPosition);

        // drive backwards
        if (this.inputDistanceInches < 0) {
            finished = currentPosition <= desiredPosition;
        } else {
            finished = currentPosition >= desiredPosition;
        }

        return finished;

    }

    // Called once after isFinished returns true
    @Override
    public void end() {
        this.driveTrain.robotDrive.stopMotor();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    public void interrupted() {
        end();
    }
}
