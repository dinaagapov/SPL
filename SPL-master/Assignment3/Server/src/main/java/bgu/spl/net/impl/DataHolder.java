package bgu.spl.net.impl;


import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataHolder {

    private ConcurrentHashMap<String , LinkedList<String>> Ifollow = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String , LinkedList<String>> myFollowers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String , LinkedList<String>> privateMessagesIpost = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer , String> id_VS_username = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String ,LinkedList<String>> receivedPost = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String , LinkedList<String>> postsIpost = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String , LinkedList<String>> privateMessageIgot = new ConcurrentHashMap<>();
    private final List<String> BannedWords = new LinkedList<String>();


    // here are all the registered user - if the user is loggedIn the loggedIn field would be true;
    private ConcurrentHashMap<String , User > Users = new ConcurrentHashMap<>();



    private static class DataHolderH{
        private static DataHolder instance = new DataHolder();
    }

    private DataHolder(){}

    public static DataHolder getInstance(){
        return DataHolderH.instance;
    }


    public boolean Register( int connectionID ,String userName ,String password , String birthDay){
        if(!Users.containsKey(userName)) {
            User newUser = new User(userName, password, birthDay);
            Users.putIfAbsent(userName , newUser);
            id_VS_username.putIfAbsent(connectionID,userName);
            Ifollow.putIfAbsent(newUser.getUserName() , new LinkedList<>());
            myFollowers.putIfAbsent(newUser.getUserName() , new LinkedList<>());
            postsIpost.putIfAbsent(newUser.getUserName() , new LinkedList<>());
            privateMessagesIpost.putIfAbsent(newUser.getUserName() , new LinkedList<>());
            receivedPost.putIfAbsent(newUser.getUserName() , new LinkedList<>());
            privateMessagesIpost.putIfAbsent(newUser.getUserName() , new LinkedList<>());
            privateMessageIgot.putIfAbsent(newUser.getUserName() , new LinkedList<>());
            postsIpost.putIfAbsent(newUser.getUserName() , new LinkedList<>());
            return true;
        }
        return false;
    }

    public boolean userIsLoggedIn(String username){
        return Users.get(username).loggedIn;
    }

    public boolean Login(int connectionID, String userName, String password, byte captcha){
        if(Users.containsKey(userName) && Users.get(userName).getPassword().equals(password)) {
            User user = Users.get(userName);
            id_VS_username.putIfAbsent(connectionID,userName);
            if(user.loggedIn || captcha != 49)
                return false;
            user.setLoggedIn(true ,connectionID);
            return true;
        }
        return false;
    }

    public boolean Logout(int connectionID){
        if(Users.containsKey(id_VS_username.get(connectionID))){
            Users.get(id_VS_username.get(connectionID)).setLoggedIn(false,connectionID);
            return true;
        }
        else return false;
    }

    public boolean FollowOrUnFollow(int connectionID, String username, byte followUnfollow) {
        if(Users.containsKey(id_VS_username.get(connectionID)) && Users.get(id_VS_username.get(connectionID)).loggedIn) {
            if (followUnfollow - '0' == 0)
                return FollowUser(connectionID, username);
            else
                return UnFollowUser(connectionID, username);
        }
        else return false;
    }

    private boolean FollowUser(int connectionID, String username) {
        String userNameThis =  getUserName(connectionID);
        if(Users.get(username) != null && userIsLoggedIn(userNameThis) && !Ifollow.get(userNameThis).contains(username) && getUsers().get(username)!=null && !getUser(connectionID).isBlockedMe(getUsers().get(username))) {
            Ifollow.get(userNameThis).add(username);
            myFollowers.get(username).add(userNameThis);
            return true;
        }
        return false;
    }
    public boolean UnFollowUser(int connectionID, String username){
        String userNameThis =  getUserName(connectionID);
        if(Users.get(username) != null && userIsLoggedIn(userNameThis) && Ifollow.get(userNameThis).contains(username)) {
            Ifollow.get(userNameThis).remove(username);
            myFollowers.get(username).remove(userNameThis);
            return true;
        }
        return false;
    }
    public LinkedList<String> PostMessage(int connectionID, String content , String[] arr) {
        String userNameThis =  getUserName(connectionID);
        if(userIsLoggedIn(userNameThis)){
            LinkedList<String> usersRecivePost = myFollowers.get(userNameThis);
            for (String userName : usersRecivePost) {
                if(Users.containsKey(userName) && !getUser(connectionID).isBlockedMe(getUsers().get(userName)))
                    receivedPost.get(userName).add(filterMessage(content));
            }
            for (String s : arr)
            {
                usersRecivePost.add(s);
            }
            postsIpost.get(userNameThis).add(filterMessage(content));
            return usersRecivePost;
        }
        else
            return null;
    }

    public boolean PrivateMessage(int connectionID, String username, String content) {
        String userNameThis = getUserName(connectionID);
        if (userIsLoggedIn(userNameThis) &&  Ifollow.get(userNameThis).contains(username) && !getUser(connectionID).isBlockedMe(getUsers().get(username))) {
            privateMessageIgot.get(userNameThis).add(filterMessage(content));
            privateMessagesIpost.get(username).add(userNameThis);
            return true;
        }
        return false;
    }
    public boolean LogstatMessage(int connectionID, String username){
        if (Users.containsKey(username) && userIsLoggedIn(username)&&!getUser(connectionID).isBlockedMe(getUsers().get(username))){
            return true;
        }
        return false;
    }
    public boolean statMessage(int connectionID, String username) {
        if (Users.containsKey(username) && userIsLoggedIn(username) &&!getUser(connectionID).isBlockedMe(getUsers().get(username))){
            return true;
        }
        return false;
    }
    public int getUserId(String username){
        int found = -1;
        for(Map.Entry<Integer,String> e : id_VS_username.entrySet()){
            if(e.getValue().equals(username))
                found = e.getKey();
        }
        return found;
    }
    public boolean blockUser(int connectionIDuserWantToBlock , String userToBlocked)
    {
        String userWantToBlock = getUserName(connectionIDuserWantToBlock);
        if (userWantToBlock != null && userIsLoggedIn(userWantToBlock))
        {
            getUser(connectionIDuserWantToBlock).addBlockUser(getUser(connectionIDuserWantToBlock));
            UnFollowUser(connectionIDuserWantToBlock, userToBlocked);
            UnFollowUser(getUserId(userToBlocked), getUserName(connectionIDuserWantToBlock));
            return true;
        }
        else
        {
            return false;
        }
    }
    public boolean isFollow(String username , String ISfollowMe )
    {
        return myFollowers.get(username).contains(ISfollowMe);
    }
    public String filterMessage(String contentToFilter)
    {
        String[] newContent = contentToFilter.split(" ");
        for (int i=0; i< newContent.length;i++)
        {
            if (BannedWords.contains(newContent[i]))
            {
                newContent[i]="<filtered>";
            }
        }
        String newContentSen = "";
        for (String s: newContent)
        {
            newContentSen = newContentSen + " "+s;
        }
        return newContentSen;
    }



    public int userAge(String username){
        return Users.get(username).getAge();
    }
    public int numberOfPosts(String username){
        return postsIpost.get(username).size();
    }
    public int numberOfFollowers(String username){
        return myFollowers.get(username).size();
    }
    public int numberOfFollowing(String username){
        return Ifollow.get(username).size();
    }
    public ConcurrentHashMap<String , User> getUsers(){
        return Users;
    }
    public String getUserName(int connectionID){
        return Users.get(id_VS_username.get(connectionID)).getUserName();
    }
    public User getUser(int connectionID){
        String s = id_VS_username.get(connectionID);
        User u = Users.get(s);
        return u;
    }
    public ConcurrentHashMap<String, LinkedList<String>> getReceivedPost() {
        return receivedPost;
    }
    public ConcurrentHashMap<String, LinkedList<String>> getPostsIpost() {
        return postsIpost;
    }
    public void ADD_DATA_FOR_TEST()
    {
        Register(1,"omer","s","17-03-2000");
        Register(1,"tal","g","11-04-2000");
        Register(1,"maya","g","17-08-2000");
        Register(1,"yonatan","s","12-04-2008");
        Ifollow.get("omer").add("yonatan");
        Ifollow.get("omer").add("maya");
        Ifollow.get("maya").add("yonatan");
        myFollowers.get("maya").add("omer");
        myFollowers.get("yonatan").add("omer");
        myFollowers.get("yonatan").add("maya");
    }


}

