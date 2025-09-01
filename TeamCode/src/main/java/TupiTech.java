package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="TupiCG")
public class TupiCG extends LinearOpMode {

    // Motores de movimentação
    private DcMotor LeftFront, LeftBack, RightFront, RightBack;

    // Motores e servos da garra
    private DcMotor CasUp, IntakeClaw, ColetaClaw;
    private Servo Garra, GarraFechar;

    @Override
    public void runOpMode() {
        // Inicialização dos motores
        LeftFront  = hardwareMap.get(DcMotor.class, "LF");
        LeftBack   = hardwareMap.get(DcMotor.class, "LB");
        RightFront = hardwareMap.get(DcMotor.class, "RF");
        RightBack  = hardwareMap.get(DcMotor.class, "RB");

        CasUp       = hardwareMap.get(DcMotor.class,"CasUp");
        IntakeClaw  = hardwareMap.get(DcMotor.class,"IntakeClaw");
        ColetaClaw  = hardwareMap.get(DcMotor.class,"ColetaClaw");

        Garra        = hardwareMap.get(Servo.class, "Garra");
        GarraFechar  = hardwareMap.get(Servo.class, "GarraFechar");

        double ServoGarraPosition = 0.47;
        double ServoGarraFecharPosition = 0.37;
        double GarraSpeed = 0.02;
        double GarraFecharSpeed = 0.02;
        double DefaultPosition = 0.47;

        LeftFront.setDirection(DcMotor.Direction.FORWARD);
        LeftBack.setDirection(DcMotor.Direction.FORWARD);
        RightFront.setDirection(DcMotor.Direction.REVERSE);
        RightBack.setDirection(DcMotor.Direction.REVERSE);

        // Variável de marcha
        double speedFactor = 1.0;

        waitForStart();
        while (opModeIsActive()) {

            // Troca de marcha
            if (gamepad1.dpad_down) {
                speedFactor = 0.3; // Marcha 1 - lenta
            } else if (gamepad1.dpad_left) {
                speedFactor = 0.6; // Marcha 2 - média
            } else if (gamepad1.dpad_up) {
                speedFactor = 1.0; // Marcha 3 - rápida
            }

            // Movimentação do robô com fator de velocidade
            double Vertical = - gamepad1.right_stick_x * speedFactor;
            double Horizontal = - gamepad1.right_stick_y * speedFactor;
            double Pivot = - gamepad1.left_stick_x * speedFactor;

            RightFront.setPower((-Pivot+(Horizontal-Vertical)));
            RightBack.setPower((-Pivot+(Horizontal+Vertical)));
            LeftFront.setPower((Pivot+(Horizontal+Vertical)));
            LeftBack.setPower((Pivot+(Horizontal-Vertical)));

            // Controles da garra
            float CascadingUp = gamepad2.right_trigger;
            float CascadingDown = gamepad2.left_trigger;
            double Intake = gamepad2.right_stick_y;
            double rot = gamepad2.left_stick_y;

            if (gamepad2.options) {
                ServoGarraPosition = DefaultPosition;
            }
            if (gamepad2.left_bumper) {
                ServoGarraPosition += GarraSpeed;
            }
            if (gamepad2.right_bumper) {
                ServoGarraPosition -= GarraSpeed;
            }
            if (gamepad2.a) {
                ServoGarraFecharPosition += GarraFecharSpeed;
            }
            if (gamepad2.b) {
                ServoGarraFecharPosition -= GarraFecharSpeed;
            }

            // Corrige valores para evitar que saiam do intervalo
            ServoGarraPosition = Math.min(Math.max(ServoGarraPosition, 0.05), 0.77);
            ServoGarraFecharPosition = Math.min(Math.max(ServoGarraFecharPosition, 0.37), 0.47);

            Garra.setPosition(ServoGarraPosition);
            GarraFechar.setPosition(ServoGarraFecharPosition);

            CasUp.setPower(CascadingUp - CascadingDown);
            IntakeClaw.setPower(Intake);
            ColetaClaw.setPower(rot);

            // Telemetria
            telemetry.addData("Marcha (velocidade)", speedFactor);
            telemetry.addData("GarraPos", ServoGarraPosition);
            telemetry.addData("GarraFecharPos", ServoGarraFecharPosition);
            telemetry.update();
        }
    }
}
