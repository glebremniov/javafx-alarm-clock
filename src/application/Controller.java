package application;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Controller {
    public Label labelSeconds;
    public Label labelMinutes;
    public Label labelHours;
    public ImageView imageSeconds;
    public ImageView imageMinute;
    public ImageView imageHour;
    public ComboBox<String> comboHH;
    public ComboBox<String> comboMM;
    public Rectangle alarmRectangle;
    public Label labelInfo;
    public ImageView imageRolex;
    private short currentMinutes;   // Переменная для текущей минуты
    private short currentHours;     // Переменная для текущего часа
    private short alarmMinutes = -1;     // Переменная для будильника(минуты)
    private short alarmHours = -1;       // Переменная для будильника (часы)
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss"); // Для корректного преобразования текущего времени
    private boolean isTurnOff= false;

    @FXML
    protected void initialize() {
        new Thread(task1).start();
        new Thread(task2).start();
//        new Thread(task3).start();  // todo На слабых машинах рекоммендуется отключить!
        String [] arrayMinutes = new String[61];    //Массивы из значений для выбора времени будильника
        String [] arrayHours = new String[25];      // 61 и 25 для того чтобы было еще специальное значение none которое отключает будильник
        arrayMinutes[0] = "none";
        arrayHours[0] = "none";
        for (int i = 1; i < 61; i++){
            arrayMinutes[i] = i < 11 ? "0" + String.valueOf(i-1) : String.valueOf(i-1);
        }
        for (int i = 1; i < 25; i++){
            arrayHours[i] = i < 11 ? "0" + String.valueOf(i-1) : String.valueOf(i-1);
        }
        ObservableList<String> listHours = FXCollections.observableArrayList(arrayHours); //ObservableList для правильного взаимодействия с ComboBox
        ObservableList<String> listMinutes = FXCollections.observableArrayList(arrayMinutes);
        comboHH.setItems(listHours); //Заполняем комбобоксы значениями
        comboMM.setItems(listMinutes);
        comboHH.setValue(null);
        comboMM.setValue(null);
    }

    private Task<Void> task1 = new Task<Void>() {   //Конструкция для правильного взаимодействия с UI потоками
        protected Void call() throws Exception {
            for (int i = 0; i < 72000; i++) { //Чтобы изменить время работы программы нужно изменить условие i<72000
                Thread.sleep(500);
                Platform.runLater(() -> showRealTime()); //Вызов фунцции будет отсуществляться 72000 раз с интервалом в полсекунды, т.е в течение 36000 секунд
            }
            return null;
        }
    };

    private Task<Void> task2 = new Task<Void>() {   //По аналогии делаем task для будильника
        protected Void call() throws Exception {
            for (int i = 0; i < 72000; i++) {
                Thread.sleep(500);
                Platform.runLater(() -> checkAlarm());
            }
            return null;
        }
    };
    private Task<Void> task3 = new Task<Void>() {
        protected Void call() throws Exception {
            for (int i = 0; i < 500; i++) {
                Thread.sleep(10);
                Platform.runLater(() -> rolex());
            }
            imageRolex.setOpacity(1.0);
            return null;
        }
    };
    private void rolex(){
        imageRolex.setOpacity(0.5 + (Math.random() * 1.0));
    }

    private void checkAlarm(){
        if (currentHours==alarmHours && currentMinutes==alarmMinutes){
            if (!isTurnOff) {
                labelInfo.setText("Нажмите сюда, чтобы\nотключить будильник.");
                comboMM.setVisible(false);
                comboHH.setVisible(false);
                alarmRectangle.setVisible(false);
                Animation.animateNode(labelInfo);
                labelHours.setTextFill(Color.valueOf("#E4C146"));
                labelMinutes.setTextFill(Color.valueOf("#E4C146"));
            }
        }
    }
    private void showRealTime() {
        String currentTime = LocalTime.now().format(dateTimeFormatter);
        String[] strings = currentTime.split(":");
        currentHours = Short.valueOf(strings[0]);
        currentMinutes = Short.valueOf(strings[1]);
        short currentSeconds = Short.valueOf(strings[2]);
        imageHour.setRotate(currentHours * 30 + currentMinutes/2);
        imageMinute.setRotate(currentMinutes * 6 + currentSeconds /10);
        imageSeconds.setRotate(currentSeconds * 6);

        labelHours.setText(currentHours < 10 ? "0" + String.valueOf(currentHours) : String.valueOf(currentHours));
        labelMinutes.setText(currentMinutes < 10 ? "0" + String.valueOf(currentMinutes) : String.valueOf(currentMinutes));
        labelSeconds.setText(currentSeconds < 10 ? "0" + String.valueOf(currentSeconds) : String.valueOf(currentSeconds));
    }

    public void actionAddAlarm(){ //Метод срабатывает при нажатии на кнопку "+"
        System.out.println("Add alarm");
        comboHH.setVisible(true);
        comboMM.setVisible(true);
        comboHH.setValue(String.valueOf(currentHours));
        comboMM.setValue(String.valueOf(currentMinutes + 1));
        comboMM.setValue(currentMinutes + 1 <10 ? "0" + String.valueOf(currentMinutes + 1) : String.valueOf(currentMinutes + 1));
        alarmRectangle.setVisible(true);
    }

    public void actionPlayAlarm(){ //Метод срабатывает при взаимодействии с комбобоксами
        if (comboMM.getValue()!=null && comboHH.getValue()!=null){
            if (comboMM.getValue().equals("none") || comboHH.getValue().equals("none")){
                labelInfo.setText("Будильник отключён.");
                comboHH.setVisible(false);
                comboMM.setVisible(false);
                alarmRectangle.setVisible(false);
                isTurnOff = true;
            }
            else {
                isTurnOff = false;
                alarmMinutes = Short.parseShort(comboMM.getValue());
//                alarmMinutes = alarmMinutes>=59? alarmMinutes : alarmMinutes++;
                alarmHours = Short.parseShort(comboHH.getValue());
                comboHH.setVisible(true);
                comboMM.setVisible(true);
                labelInfo.setText("Будильник установлен на " + (alarmHours<10 ? "0" + alarmHours : alarmHours) + ":" + (alarmMinutes<10 ? "0" + alarmMinutes : alarmMinutes) + ".\nНажмите, чтобы отключить.");
                System.out.println("Alarm time: " + alarmHours + ":" + alarmMinutes);
            }
        }
    }
    public void actionTurnOffAlarm(){ //Метод для отключение будильника, срабатывает при нажатии на labelInfo
        if (!isTurnOff) {
            isTurnOff = true;
            comboHH.setValue("none");
            comboMM.setValue("none");
            labelHours.setTextFill(Color.valueOf("#FFFFFF"));
            labelMinutes.setTextFill(Color.valueOf("#FFFFFF"));
            labelInfo.setText("Будильник отключён.");
            System.out.println("Будильник отключён.");
        }

    }
}