using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ArrowScript : MonoBehaviour
{
    public float speed = 5f;
    public float maxDistance = 5f;

    private bool isMoving = false;
    private Vector3 startPosition;
    private float distanceMoved = 0f;

    void Start()
    {
        // Hide the arrow below the ground at start
        transform.position = new Vector3(transform.position.x, -500f, transform.position.z);
    }

    void Update()
    {
        if (isMoving)
        {
            // Move the arrow towards the table
            transform.position = Vector3.MoveTowards(transform.position, startPosition, speed * Time.deltaTime);
            distanceMoved += speed * Time.deltaTime;

            // If the arrow has moved past the max distance, stop it
            if (distanceMoved >= maxDistance)
            {
                isMoving = false;
            }
        }
    }

    public void StartArrowMovement(Vector3 startPos)
    {
        // Set the start position and start moving the arrow
        startPosition = startPos;
        isMoving = true;
        distanceMoved = 0f;
    }
}