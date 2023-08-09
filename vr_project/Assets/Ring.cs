using UnityEngine;

public class Ring : MonoBehaviour {

    public GameObject Bridge;
    public GameObject Wall;
    AudioSource audioSource;

    void Start()
    {
        audioSource = GetComponent<AudioSource>();
    }

    public void OnTriggerExit(Collider other)
    {
        if (other.gameObject.tag == "Ball")
        {
            Bridge.SetActive(true);
            audioSource.Play();
            //Destroy(gameObject);
            Wall.transform.localPosition = new Vector3(0, -40, 0);
            this.transform.localPosition = new Vector3(0, -5, 0);
        }
    }
}