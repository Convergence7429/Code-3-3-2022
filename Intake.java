package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.I2C;
import com.revrobotics.CANSparkMax.ControlType;
import edu.wpi.first.wpilibj.Timer;

public class Intake {

    CANSparkMax intakeMotor = new CANSparkMax(Constants.intakeMotorIndex, MotorType.kBrushless);
    CANSparkMax indexerMotor = new CANSparkMax(Constants.indexerMotorIndex, MotorType.kBrushless);
    CANSparkMax intakeAngleMotor = new CANSparkMax(Constants.intakeAngleMotorIndex, MotorType.kBrushless);

    I2C.Port i2cPort = I2C.Port.kOnboard;
    ColorSensorV3 indexerColorSensor = new ColorSensorV3(i2cPort);

    static boolean intaking = false;
    static boolean isIntakeDown = false;
    static boolean movingIntake = false;

    Timer intakeAngleTimer = new Timer();

    public void intakeInit() {
        intaking = false;
        isIntakeDown = false;
        movingIntake = false;

        intakeMotor.enableVoltageCompensation(12.5);
        indexerMotor.enableVoltageCompensation(12.5);
        intakeAngleMotor.enableVoltageCompensation(12.5);

        intakeAngleTimer.reset();
        intakeAngleTimer.start();
    }

    public void indexByColor() {
        Color detectedColor = indexerColorSensor.getColor();

        if (!Robot.shooter.shooting && !Robot.shooter.dumpShot) {
            if ((Math.abs(detectedColor.red - 0.295) < 0.025) || (Math.abs(detectedColor.blue - 0.2975) < 0.015)) {
                indexerMotor.set(0.0);
            } else {
                indexerMotor.set(-0.2);
            }
        }
    }

    public void intakeTeleop() {

        if (isIntakeDown) {
            if (Constants.stick.getRawButton(10)) {
                intakeMotor.set(0.6);
                intaking = false;
            } else if (Constants.stick.getRawButtonPressed(1)) {
                intaking = !intaking;
                if (intaking) {
                    intakeMotor.set(-0.8);
                }
            } else {
                if (!intaking) {
                    intakeMotor.set(0.0);
                }
            }
        } else {
            intakeMotor.set(0.0);
        }

        if (!Robot.shooter.shooting && !Robot.shooter.dumpShot) {
            if (Constants.xbox.getPOV() == 0) {
                indexerMotor.set(-0.4);
            } else if (Constants.xbox.getPOV() == 180) {
                indexerMotor.set(0.4);
            } else { // either rewrite method in same way or just call method
                indexByColor();
            }
        }

        if ((Constants.xbox.getRawButtonPressed(2)) && (intakeAngleTimer.get() > 1.25)) { // B Button?
            movingIntake = true;
            intakeAngleTimer.reset();
            intakeAngleTimer.start();
        }

        if (movingIntake && !isIntakeDown) {
            intakeAngleMotor.set(-0.5);
            if (intakeAngleTimer.get() > 1.15) {
                intakeAngleMotor.set(0.0);
                intakeAngleMotor.getPIDController().setReference(intakeAngleMotor.getEncoder().getPosition(),
                        ControlType.kPosition);
                movingIntake = false;
                isIntakeDown = true;
            }
        }

        if (movingIntake && isIntakeDown) {
            intakeAngleMotor.set(0.5);
            if (intakeAngleTimer.get() > 1.25) {
                intakeAngleMotor.set(0.0);
                intakeAngleMotor.getPIDController().setReference(intakeAngleMotor.getEncoder().getPosition(),
                        ControlType.kPosition);
                movingIntake = false;
                isIntakeDown = false;
            }
        }
    }
}

// if (Constants.stick.getRawButtonPressed(1)) {
// intaking = !intaking;
// }

// if(Constants.stick.getRawButton(10)){
// intakeMotor.set(intakeSpeed);
// } else {
// if (intaking) {
// intakeMotor.set(-intakeSpeed);
// } else {
// intakeMotor.set(0.0);
// }
// }

// if (Constants.stick.getRawButton(11)) { // needs some work
// intakeAngleMotor.set(-0.35);
// } else if (Constants.stick.getRawButton(12)) {
// intakeAngleMotor.set(0.35);
// } else {
// intakeAngleMotor.set(0.0);
// intakeAngleMotor.getPIDController().setReference(intakeAngleMotor.getEncoder().getPosition(),
// ControlType.kPosition);
// }

// if (Constants.stick.getRawButton(10)) {
// intakeMotor.set(0.6);
// intaking = false;
// } else if (Constants.stick.getRawButtonPressed(1)) {
// if (isIntakeDown) {
// intaking = !intaking;
// if (intaking && isIntakeDown) {
// intakeMotor.set(-0.6);
// }
// }
// } else {
// if (!intaking) {
// intakeMotor.set(0.0);
// }
// }