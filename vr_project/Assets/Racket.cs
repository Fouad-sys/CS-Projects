using UnityEngine;
using OculusSampleFramework;

public class Racket : MonoBehaviour
{
    public Vector3 Velocity { get; private set; }

    private Vector3 lastPosition;

    private Rigidbody rigidbody;

    private void Start()
    {
        lastPosition = transform.position;
        rigidbody = GetComponent<Rigidbody>();
    }

    private void Update()
    {
        if (rigidbody.isKinematic) //attached to the hand controller
        {
            Velocity = (transform.position - lastPosition) / Time.deltaTime;
            Debug.DrawRay(transform.position, Velocity, Color.blue, 0.1f);
        }
        else
        {
            Velocity = rigidbody.velocity;
        }
        lastPosition = transform.position;
    }
}