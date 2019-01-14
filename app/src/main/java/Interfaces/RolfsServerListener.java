package Interfaces;

import java.util.ArrayList;

import Entities.Group;
import Entities.Member;

public interface RolfsServerListener {

    ArrayList<Member> memberRequest();

    void groupRequest(ArrayList<String> groups);

    void onPositionsMessage(ArrayList<Member> members);

}
