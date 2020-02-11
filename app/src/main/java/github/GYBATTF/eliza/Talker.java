package github.GYBATTF.eliza;

public interface Talker {
    String talk(String msg);
    boolean finished();
    String getInitial();
}
