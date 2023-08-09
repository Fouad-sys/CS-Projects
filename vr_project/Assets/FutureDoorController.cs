using UnityEngine;

public class FutureDoorController : MonoBehaviour
{
    public GameObject rightDoor;
    public GameObject leftDoor;
    public GameObject keyLock;
    private bool isOpen = false;
    private bool isOpening = false;
    //private bool isClosing = false;

    private Vector3 displacmentVector = new Vector3(0.6f, 0, 0);
    private float movementSpeed = 0.01f;
    private Vector3 closedPositionRight;
    private Vector3 closedPositionLeft;
    private Vector3 closedPositionLock;
    private float t = 0.0f;

    AudioSource doorOpenSound;

    public void openDoor(){
        isOpening = true;
        doorOpenSound.Play();
    }

    private void Start(){
        // Set the initial position of the door
        closedPositionRight = rightDoor.transform.position;
        closedPositionLeft = leftDoor.transform.position;
        closedPositionLock = keyLock.transform.position;
        doorOpenSound = GetComponent<AudioSource>();
    }

    private void Update(){
        if(isOpening){
            // Calculate the new position using linear interpolation
            // Handle transition rate
                t += movementSpeed;

                // Cap values
                if ( t < 0 ) t = 0;
                if ( t > 1 ) t = 1;

                // Animate the fence
                rightDoor.transform.position = ( 1 - t ) * closedPositionRight + t * (closedPositionRight-displacmentVector);
                keyLock.transform.position = ( 1 - t ) * closedPositionLock + t * (closedPositionLock+displacmentVector);
                leftDoor.transform.position = ( 1 - t ) * closedPositionLeft + t * (closedPositionLeft+displacmentVector);

            if(rightDoor.transform.position == closedPositionRight+displacmentVector){
                isOpening = false;
            }
        }
    }
}