using UnityEngine;

public class Key : MonoBehaviour {

        public GameObject door;

	public void OnTriggerEnter ( Collider player ) { 
        // door.GetComponent<FutureDoorController>().openDoor();
        // rotate the door 90 degrees in x
        door.transform.localPosition = new Vector3(3, 0, 0);
        }

}