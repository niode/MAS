package Agent;

public enum State
{
  NO_STATE(0),
  TEAM_SEARCH((1 << 0));

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
