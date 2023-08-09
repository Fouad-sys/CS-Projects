using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PlateScript : MonoBehaviour
{
    public bool hasCake = false;

    private void OnCollisionEnter(Collision collision)
    {
        Debug.LogWarningFormat( "Plate collision" );
        // Check if the other collider is the cake object
        if (collision.gameObject.CompareTag("Cake"))
        {
            Debug.LogWarningFormat( "Has cake" );
            // Attach the cake to the plate
            collision.transform.parent = transform;
            hasCake = true;
        }
    }
}