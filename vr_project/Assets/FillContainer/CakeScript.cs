using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CakeScript : MonoBehaviour
{
    private Rigidbody rb;

    private void Start()
    {
        // Get the Rigidbody component attached to the cake object
        rb = GetComponent<Rigidbody>();
    }

    private void OnCollisionEnter(Collision collision)
    {
        // Check if the other collider is the plate object
        if (collision.gameObject.CompareTag("Plate"))
        {
            // Disable the Rigidbody component on the cake object
            rb.isKinematic = true;
            // rb.useGravity = false;

            // Set the parent of the cake object to the plate object
            transform.parent = collision.transform;
        }
        // // Ignore collisions with objects that don't have a "Cake" or "Plate" tag
        // else if (!collision.gameObject.CompareTag("Cake") && !collision.gameObject.CompareTag("Plate"))
        // {
        //     Physics.IgnoreCollision(GetComponent<BoxCollider>(), collision.collider);
        // }
    }
}