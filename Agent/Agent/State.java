package Agent;

public enum State
{
  TEAM_SEARCH(0);

  private int val;
  private State(int val)
  {
    this.val = val;
  }

  public int value()
  {
    return val;
  }
}
