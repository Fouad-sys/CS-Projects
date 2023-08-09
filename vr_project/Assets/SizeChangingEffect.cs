using UnityEngine;

public class SizeChangingEffect : MonoBehaviour
{
    public float minScale = 1f;     // Minimum scale of the object
    public float maxScale = 3f;       // Maximum scale of the object
    public float scaleSpeed = 1f;     // Speed at which the object scales

    private bool isGrowing = true;    // Flag to determine if the object is currently growing or shrinking

    void Update()
    {
        // Check if the object is growing or shrinking
        if (isGrowing)
        {
            // Increase the scale of the object over time
            transform.localScale += Vector3.one * scaleSpeed * Time.deltaTime;

            // Check if the object has reached the maximum scale
            if (transform.localScale.x >= maxScale)
                isGrowing = false;   // Set the flag to start shrinking
        }
        else
        {
            // Decrease the scale of the object over time
            transform.localScale -= Vector3.one * scaleSpeed * Time.deltaTime;

            // Check if the object has reached the minimum scale
            if (transform.localScale.x <= minScale)
                isGrowing = true;    // Set the flag to start growing again
        }
    }
}