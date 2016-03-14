package hungry.redball.mongo;

public interface mongoInterface {
        void setTime(int hour, int minute, int second);
        void setDate(int day, int month, int year);
        void setDateAndTime(int day, int month, int year,
                            int hour, int minute, int second);
}
