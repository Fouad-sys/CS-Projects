using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class TableScript : MonoBehaviour
{
    public GameObject arrow;
    public GameObject door;

    private void OnCollisionEnter(Collision collision)
    {
        // Check if the other collider is the plate object
        if (collision.gameObject.CompareTag("Plate"))
        {
            Debug.LogWarningFormat( "Table collision" );
            // Check if the Plate object hasCake is true
            if (collision.gameObject.GetComponent<PlateScript>().hasCake)
            {
                Debug.LogWarningFormat( "Table has cake" );
                // // Change the table color to red
                // GetComponent<Renderer>().material.color = Color.red;

                // Make arrow appear
                arrow.transform.localPosition = new Vector3(0, 0, -3);

                // Open the door
                door.transform.localRotation = Quaternion.Euler(0, -75, 0);
            }
        }
    }
}